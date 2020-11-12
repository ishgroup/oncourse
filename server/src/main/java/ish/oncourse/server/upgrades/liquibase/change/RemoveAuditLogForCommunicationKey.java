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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RemoveAuditLogForCommunicationKey extends IshTaskChange {

    private static final int BATCH_SIZE = 100;
    private static final String AUDIT_TABLE_NAME = "Audit";
    private static final String SELECT_PREFERENCE_ID_QUERY = "select a.id from Preference p\n" +
            "join Audit a on p.id = a.entityId\n" +
            "where p.name = 'services.soap.communication.key'";

    @Override
    public void execute(Database database) throws CustomChangeException{
        try {
            var connection = (JdbcConnection) database.getConnection();

            var idsToRemove = fetchIds(connection, SELECT_PREFERENCE_ID_QUERY);

            var stmt = connection.createStatement();

            for (var query : IshTaskChangeUtils.getDeleteQueryBatch(AUDIT_TABLE_NAME, idsToRemove, BATCH_SIZE)) {
                stmt.addBatch(query);
            }

            stmt.executeBatch();
            connection.commit();
            stmt.close();
        } catch (Exception ex) {
            throw new CustomChangeException("Removing audit log records for communication key failed:", ex);
        }
    }

    protected List<Long> fetchIds(JdbcConnection connection, String query) throws DatabaseException, SQLException {
        List<Long> ids = new ArrayList<>();

        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(query);
        while(resultSet.next()) {
            ids.add(resultSet.getLong("id"));
        }
        return ids;
    }
}
