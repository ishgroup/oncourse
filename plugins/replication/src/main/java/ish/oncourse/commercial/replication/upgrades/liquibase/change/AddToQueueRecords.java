/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.upgrades.liquibase.change;

import ish.oncourse.server.upgrades.liquibase.change.IshTaskChange;
import ish.util.SecurityUtil;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddToQueueRecords extends IshTaskChange {

    private static final String SQL_CREATE_QUEUED_TRANSACTION = "INSERT INTO QueuedTransaction (createdOn, modifiedOn, transactionKey) VALUES (?, ?, ?)";
    private static final String SQL_CREATE_QUEUED_RECORD = "INSERT INTO QueuedRecord (action, numberOfAttempts, transactionId, createdOn, modifiedOn, lastAttemptOn, tableName, foreignRecordId, willowId) " +
            "VALUES (?, 0, (SELECT id FROM QueuedTransaction WHERE transactionKey = ?), ?, ?, ?, ?, ? ,?)";

    private Map<Long, Long> records = new HashMap<>();

    private String entityName;
    private String selectQuery;
    private String action = "UPDATE";

    @Override
    public void execute(Database database) throws CustomChangeException{

        try {
            var connection = (JdbcConnection) database.getConnection();

            //find records
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(selectQuery);
            while(resultSet.next()) {
                records.put(resultSet.getLong("id"), resultSet.getLong("willowId"));
            }

            resultSet.close();

            //stage of creation
            var now = new Date();
            var timestamp = new Timestamp(now.getTime());

            for (var entry : records.entrySet()) {
                var transactionKey = SecurityUtil.generateRandomPassword(32);
                //create QueuedTransaction
                var transaction = connection.prepareStatement(SQL_CREATE_QUEUED_TRANSACTION);
                transaction.setTimestamp(1, timestamp);
                transaction.setTimestamp(2, timestamp);
                transaction.setString(3, transactionKey);
                transaction.executeUpdate();
                transaction.close();

                //create QueuedRecord
                var queuedRecord = connection.prepareStatement(SQL_CREATE_QUEUED_RECORD);
                queuedRecord.setString(1, action);
                queuedRecord.setString(2, transactionKey);
                queuedRecord.setTimestamp(3, timestamp);
                queuedRecord.setTimestamp(4, timestamp);
                queuedRecord.setTimestamp(5, timestamp);
                queuedRecord.setString(6, entityName);
                queuedRecord.setLong(7, entry.getKey());
                queuedRecord.setLong(8, entry.getValue());
                queuedRecord.executeUpdate();
                queuedRecord.close();
            }

        } catch (Exception e) {
            throw new CustomChangeException("Adding records to queue failed:", e);
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
