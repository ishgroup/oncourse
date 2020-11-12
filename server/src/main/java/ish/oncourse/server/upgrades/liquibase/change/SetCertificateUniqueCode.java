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

import ish.util.SecurityUtil;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Artem on 1/09/2016.
 */
public class SetCertificateUniqueCode extends IshTaskChange {

    private static final String SQL_CREATE_QUEUED_RECORD = "UPDATE Certificate SET uniqueCode = ? WHERE id=?";

    private String selectQuery;

    private Set<Long> records = new HashSet<>();

    @Override
    public void execute(Database database) throws CustomChangeException {
        try {
            var connection = (JdbcConnection) database.getConnection();

            //find records
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(selectQuery);
            while(resultSet.next()) {
                records.add(resultSet.getLong("id"));
            }

            resultSet.close();

            for (var entry : records) {

                //create QueuedRecord
                var queuedRecord = connection.prepareStatement(SQL_CREATE_QUEUED_RECORD);
                queuedRecord.setString(1, SecurityUtil.generateCertificateCode());
                queuedRecord.setLong(2, entry);
                queuedRecord.executeUpdate();
                queuedRecord.close();
            }

        } catch (Exception e) {
            throw new CustomChangeException("Adding records to queue failed:", e);
        }
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }
}
