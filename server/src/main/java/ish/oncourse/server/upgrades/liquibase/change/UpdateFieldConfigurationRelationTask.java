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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class UpdateFieldConfigurationRelationTask extends IshTaskChange {

    private static final String SQL_SELECT_SCHEMAS = "SELECT * FROM FieldConfigurationScheme";
    private static final String SQL_INSERT_LINK = "INSERT INTO FieldConfigurationLink (createdOn, modifiedOn, schemeId, configurationId) VALUES (?, ?, ?, ?)";

    @Override
    public void execute(Database database) throws CustomChangeException {
        var now = new Date();
        var timestamp = new Timestamp(now.getTime());

        try {
            var connection = (JdbcConnection) database.getConnection();
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(SQL_SELECT_SCHEMAS);

            while(resultSet.next()) {
                Long schemeId = resultSet.getLong("id");

                Long enrolConfigurationId = resultSet.getLong("enrolFieldConfigurationId");
                createFieldConfigurationLink(connection, timestamp, schemeId, enrolConfigurationId);

                Long appConfigurationId = resultSet.getLong("applicationFieldConfigurationId");
                createFieldConfigurationLink(connection, timestamp, schemeId, appConfigurationId);

                Long wListConfigurationId = resultSet.getLong("waitingListFieldConfigurationId");
                createFieldConfigurationLink(connection, timestamp, schemeId, wListConfigurationId);
            }

            resultSet.close();
            statement.close();

        } catch (Exception e) {
            throw new CustomChangeException("Update FieldConfiguration relations failed:", e);
        }
    }

    private void createFieldConfigurationLink(JdbcConnection connection, Timestamp timestamp, Long schemeId, Long configurationId) throws Exception {
        var prepareStatement = connection.prepareStatement(SQL_INSERT_LINK);
        prepareStatement.setTimestamp(1, timestamp);
        prepareStatement.setTimestamp(2, timestamp);
        prepareStatement.setLong(3, schemeId);
        prepareStatement.setLong(4, configurationId);
        prepareStatement.executeUpdate();
        prepareStatement.close();
    }
}
