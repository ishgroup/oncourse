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

public class DuplicateFieldConfigurationTask extends IshTaskChange {

    private static final String ENROL_NAME_FORMAT = "%s (Enrolment)";
    private static final String APP_NAME_FORMAT = "%s (Application)";
    private static final String WAITLIST_NAME_FORMAT = "%s (Waiting List)";

    private static final String SQL_SELECT_FIELD_CONFIGURATIONS = "SELECT * FROM FieldConfiguration";
    private static final String SQL_INSERT_CONFIGURATION = "INSERT INTO FieldConfiguration (createdOn, modifiedOn, name, type) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_CONFIGURATION = "UPDATE FieldConfiguration SET name = ? WHERE id = ?";
    private static final String SQL_FIND_CONFIGURATION = "SELECT id FROM FieldConfiguration WHERE name = ?";

    private static final String SQL_FIND_SCHEMAS_FOR_APPLICATION = "SELECT id FROM FieldConfigurationScheme WHERE applicationFieldConfigurationId = %s";
    private static final String SQL_UPDATE_SCHEMA_FOR_APPLICATION = "UPDATE FieldConfigurationScheme SET applicationFieldConfigurationId = ? WHERE id = ?";
    private static final String SQL_FIND_SCHEMAS_FOR_WAITING_LIST = "SELECT id FROM FieldConfigurationScheme WHERE waitingListFieldConfigurationId = %s";
    private static final String SQL_UPDATE_SCHEMA_FOR_WAITING_LIST = "UPDATE FieldConfigurationScheme SET waitingListFieldConfigurationId = ? WHERE id = ?";

    private static final String SQL_FIND_FIELD_HEADINGS = "SELECT * FROM FieldHeading WHERE fieldConfigurationId = %s";
    private static final String SQL_INSERT_FIELD_HEADING = "INSERT INTO FieldHeading (createdOn, modifiedOn, fieldConfigurationId, description, name, fieldOrder) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_FIND_HEADING = "SELECT id FROM FieldHeading WHERE fieldConfigurationId = %s AND name = ?";

    private static final String SQL_FIND_FIELDS = "SELECT * FROM Field WHERE fieldConfigurationId = %s AND fieldHeadingId IS NULL";
    private static final String SQL_FIND_FIELDS_WITH_HEADING = "SELECT * FROM Field WHERE fieldConfigurationId = %s AND fieldHeadingId = %s";

    private static final String SQL_INSERT_FIELD = "INSERT INTO Field (createdOn, modifiedOn, fieldConfigurationId, description, name, defaultValue, mandatory, fieldOrder, property) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_FIELD_WITH_HEADING = "INSERT INTO Field (createdOn, modifiedOn, fieldHeadingId, fieldConfigurationId, description, name, defaultValue, mandatory, fieldOrder, property) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void execute(Database database) throws CustomChangeException {
        var now = new Date();
        var timestamp = new Timestamp(now.getTime());

        try {
            var connection = (JdbcConnection) database.getConnection();
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(SQL_SELECT_FIELD_CONFIGURATIONS);

            while(resultSet.next()) {
                Long enrolId = resultSet.getLong("id");
                var name = resultSet.getString("name");

                var enrolConf = connection.prepareStatement(SQL_UPDATE_CONFIGURATION);
                enrolConf.setString(1, String.format(ENROL_NAME_FORMAT, name));
                enrolConf.setLong(2, enrolId);
                enrolConf.executeUpdate();
                enrolConf.close();

                //-------------

                var appConfName = String.format(APP_NAME_FORMAT, name);
                createConfiguration(connection, timestamp, appConfName, 2);
                var appConfId = getId(connection, SQL_FIND_CONFIGURATION, appConfName);
                updateScheme(connection,  String.format(SQL_FIND_SCHEMAS_FOR_APPLICATION, enrolId), SQL_UPDATE_SCHEMA_FOR_APPLICATION, appConfId);
                createChildren(connection, enrolId, appConfId, timestamp);

                //-------------

                var wListConfName = String.format(WAITLIST_NAME_FORMAT, name);
                createConfiguration(connection, timestamp, wListConfName, 3);
                var wListConfId = getId(connection, SQL_FIND_CONFIGURATION, wListConfName);
                updateScheme(connection, String.format(SQL_FIND_SCHEMAS_FOR_WAITING_LIST, enrolId), SQL_UPDATE_SCHEMA_FOR_WAITING_LIST, wListConfId);
                createChildren(connection, enrolId, wListConfId, timestamp);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            throw new CustomChangeException("FieldConfiguration duplication failed:", e);
        }
    }


    private static void createConfiguration(JdbcConnection connection, Timestamp timestamp, String name, Integer type) throws Exception {
        var appConf = connection.prepareStatement(SQL_INSERT_CONFIGURATION);
        appConf.setTimestamp(1, timestamp);
        appConf.setTimestamp(2, timestamp);
        appConf.setString(3, name);
        appConf.setInt(4, type);
        appConf.executeUpdate();
        appConf.close();
    }

    private static Long getId(JdbcConnection connection, String query, String name) throws Exception {
        var statement = connection.prepareStatement(query);
        statement.setString(1, name);
        var resultSet = statement.executeQuery();
        resultSet.next();
        Long id = resultSet.getLong("id");

        resultSet.close();
        statement.close();
        return id;
    }

    private static void updateScheme(JdbcConnection connection, String query, String queryToUpdate, Long id) throws Exception {
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            var enrolConf = connection.prepareStatement(queryToUpdate);
            enrolConf.setLong(1, id);
            enrolConf.setLong(2, resultSet.getLong("id"));
            enrolConf.executeUpdate();
            enrolConf.close();
        }
        resultSet.close();
        statement.close();
    }

    private static void createChildren(JdbcConnection connection, Long oldConfId, Long newConfId, Timestamp timestamp) throws Exception {
        createFields(connection, oldConfId, newConfId, timestamp);

        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(String.format(SQL_FIND_FIELD_HEADINGS, oldConfId));
        while(resultSet.next()) {
            Long oldId = resultSet.getLong("id");
            var name = resultSet.getString("name");
            var createHeading = connection.prepareStatement(SQL_INSERT_FIELD_HEADING);
            createHeading.setTimestamp(1, timestamp);
            createHeading.setTimestamp(2, timestamp);
            createHeading.setLong(3, newConfId);
            createHeading.setString(4, resultSet.getString("description"));
            createHeading.setString(5, name);
            createHeading.setInt(6, resultSet.getInt("fieldOrder"));
            createHeading.executeUpdate();
            createHeading.close();

            var id = getId(connection, String.format(SQL_FIND_HEADING, newConfId), name);

            createFields(connection, oldConfId, newConfId, oldId, id, timestamp);

        }

        statement.close();
        resultSet.close();

    }


    private static void createFields(JdbcConnection connection, Long oldConfId, Long newConfId, Long oldHeadingId, Long newHeadingId, Timestamp timestamp) throws Exception {
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(String.format(SQL_FIND_FIELDS_WITH_HEADING, oldConfId, oldHeadingId));

        while(resultSet.next()) {
            var createHeading = connection.prepareStatement(SQL_INSERT_FIELD_WITH_HEADING);
            createHeading.setTimestamp(1, timestamp);
            createHeading.setTimestamp(2, timestamp);
            createHeading.setLong(3, newHeadingId);
            createHeading.setLong(4, newConfId);
            createHeading.setString(5, resultSet.getString("description"));
            createHeading.setString(6, resultSet.getString("name"));
            createHeading.setString(7, resultSet.getString("defaultValue"));
            createHeading.setInt(8, resultSet.getInt("mandatory"));
            createHeading.setInt(9, resultSet.getInt("fieldOrder"));
            createHeading.setString(10, resultSet.getString("property"));
            createHeading.executeUpdate();
            createHeading.close();
        }

        statement.close();
        resultSet.close();
    }


    private static void createFields(JdbcConnection connection, Long oldConfId, Long newConfId, Timestamp timestamp) throws Exception {
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(String.format(SQL_FIND_FIELDS, oldConfId));

        while(resultSet.next()) {
            var createHeading = connection.prepareStatement(SQL_INSERT_FIELD);
            createHeading.setTimestamp(1, timestamp);
            createHeading.setTimestamp(2, timestamp);
            createHeading.setLong(3, newConfId);
            createHeading.setString(4, resultSet.getString("description"));
            createHeading.setString(5, resultSet.getString("name"));
            createHeading.setString(6, resultSet.getString("defaultValue"));
            createHeading.setInt(7, resultSet.getInt("mandatory"));
            createHeading.setInt(8, resultSet.getInt("fieldOrder"));
            createHeading.setString(9, resultSet.getString("property"));
            createHeading.executeUpdate();
            createHeading.close();
        }
        statement.close();
        resultSet.close();
    }
}
