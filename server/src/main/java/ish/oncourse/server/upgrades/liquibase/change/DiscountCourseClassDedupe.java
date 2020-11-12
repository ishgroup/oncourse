/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscountCourseClassDedupe extends IshTaskChange {

    private static final int BATCH_SIZE = 100;

    private static final String CLASSCOST_DUPLICATES_ONLY = "select c.id, c.discountCourseClassId from \n" +
            "(select min(cc.id) id, discountCourseClassId from ClassCost cc\n" +
            "where cc.discountCourseClassId is not null\n" +
            "group by cc.discountCourseClassId having count(cc.id) > 1) dup,\n" +
            "ClassCost c\n" +
            "where dup.discountCourseClassId = c.discountCourseClassId and dup.id <> c.id";

    private static final String DISCOUNT_CC_DUPLICATES_ONLY = "select discounts.id from \n" +
            "(select min(dcc.id) id, dcc.discountId, dcc.courseClassId from Discount_CourseClass dcc\n" +
            "group by dcc.courseClassId, dcc.discountId having count(dcc.id) > 1) pairs,\n" +
            "Discount_CourseClass discounts\n" +
            "where discounts.courseClassId = pairs.courseClassId and discounts.discountId = pairs.discountId and discounts.id <> pairs.id";

    private static final String DISCOUNT_CC_INVALID_RECORDS = "SELECT dcc.id FROM Discount_CourseClass dcc \n" +
            "left join ClassCost cc on cc.discountCourseClassId = dcc.id\n" +
            "where cc.id is null";

    private static final String CLASSCOST_FETCH_PATTERN = "select id from ClassCost where discountCourseClassId in (%s)";

    @Override
    public void execute(Database database) throws CustomChangeException {
        try {
            var connection = (JdbcConnection) database.getConnection();
            var batchStatement = connection.createStatement();

            removeClassCostDuplicatesAndRelatedDiscountCourseClass(batchStatement);
            removeDiscountCourseClassDuplicates(batchStatement);

            connection.commit();
        } catch (Exception ex) {
            throw new CustomChangeException("Removing duplicate relations record for Discount_CourseClass failed:", ex);
        }
    }

    private void removeClassCostDuplicatesAndRelatedDiscountCourseClass(Statement batchStatement) throws SQLException, DatabaseException {
        ResultSet resultSet;

        // get class cost duplicates and related Discount_CourseClass
        resultSet = batchStatement.executeQuery(CLASSCOST_DUPLICATES_ONLY);

        List<Long> classCostDuplicates = new ArrayList<>();
        List<Long> relatedDiscountCourseClass = new ArrayList<>();
        while(resultSet.next()) {
            classCostDuplicates.add(resultSet.getLong("id"));
            relatedDiscountCourseClass.add(resultSet.getLong("discountCourseClassId"));
        }

        if (classCostDuplicates.size() > 0) {
            // remove class cost duplicates on angel
            for (var query : IshTaskChangeUtils.getDeleteQueryBatch("ClassCost", classCostDuplicates, BATCH_SIZE)) {
                batchStatement.addBatch(query);
            }
            batchStatement.executeBatch();

            // no need to remove ClassCost on willow: table does not exist
        }
    }

    private void removeDiscountCourseClassDuplicates(Statement batchStatement) throws SQLException, DatabaseException {
        ResultSet resultSet;

        // remove Discount_CourseClass without ClassCost

        List<Long> incorrectRelations = new ArrayList<>();
        resultSet = batchStatement.executeQuery(DISCOUNT_CC_INVALID_RECORDS);

        while(resultSet.next()) {
            incorrectRelations.add(resultSet.getLong("id"));
        }

        // remove class costs
        for (var query : IshTaskChangeUtils.getDeleteQueryBatch("Discount_CourseClass", incorrectRelations, BATCH_SIZE)) {
            batchStatement.addBatch(query);
        }
        batchStatement.executeBatch();

        // get duplicate discount relation ids
        resultSet = batchStatement.executeQuery(DISCOUNT_CC_DUPLICATES_ONLY);

        List<Long> discountRelduplicateIds = new ArrayList<>();

        while(resultSet.next()) {
            discountRelduplicateIds.add(resultSet.getLong("id"));
        }

        if (!discountRelduplicateIds.isEmpty()) {

            // get related class cost ids

            resultSet = batchStatement.executeQuery(String.format(CLASSCOST_FETCH_PATTERN, StringUtils.join(discountRelduplicateIds, ",")));
            List<Long> classCostDuplicateIds = new ArrayList<>();
            while(resultSet.next()) {
                classCostDuplicateIds.add(resultSet.getLong("id"));
            }

            // remove class costs
            for (var query : IshTaskChangeUtils.getDeleteQueryBatch("ClassCost", classCostDuplicateIds, BATCH_SIZE)) {
                batchStatement.addBatch(query);
            }
            batchStatement.executeBatch();

            // add class cost ids to queued record (should be deleted from willow too)
            for (var insert : IshTaskChangeUtils.getDeleteQueuedRecordsBatch("ClassCost", discountRelduplicateIds, BATCH_SIZE, new Date())) {
                batchStatement.addBatch(insert);
            }
            batchStatement.executeBatch();

            // remove duplicate discount relations
            for (var query : IshTaskChangeUtils.getDeleteQueryBatch("Discount_CourseClass", discountRelduplicateIds, BATCH_SIZE)) {
                batchStatement.addBatch(query);
            }
            batchStatement.executeBatch();

            // add duplicate ids to queued record (should be deleted from willow too)
            for (var insert : IshTaskChangeUtils.getDeleteQueuedRecordsBatch("DiscountCourseClass", discountRelduplicateIds, BATCH_SIZE, new Date())) {
                batchStatement.addBatch(insert);
            }
            batchStatement.executeBatch();
        }
    }
}
