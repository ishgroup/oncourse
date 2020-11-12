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
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static org.apache.commons.lang3.text.WordUtils.uncapitalize;

/**
 * Created by Artem on 15/11/2016.
 */
public class FillCustomFieldTypeKeys extends IshTaskChange {

    private static final String SELECT_STATEMENT = "SELECT id AS ID, name AS NAME FROM CustomFieldType";
    private static final String UPDATE_STATEMENT = "UPDATE CustomFieldType SET fieldKey=? WHERE id=?";

    private Map<Long, String> records = new HashMap<>();

    @Override
    public void execute(Database database) throws CustomChangeException {
        try {
            var connection = (JdbcConnection) database.getConnection();

            //find records
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(SELECT_STATEMENT);
            while(resultSet.next()) {
                records.put(resultSet.getLong("ID"), resultSet.getString("NAME"));
            }
            resultSet.close();

            for (var entry : records.entrySet()) {
                var name = entry.getValue();
                var keyToCamelCase = uncapitalize(capitalizeFully(name.replaceAll("[^A-Za-z0-9 ]", "")).replaceAll(" ", ""));

                var updateStatement = connection.prepareStatement(UPDATE_STATEMENT);
                updateStatement.setString(1, keyToCamelCase);
                updateStatement.setLong(2, entry.getKey());
                updateStatement.executeUpdate();
                updateStatement.close();
            }

        } catch (Exception e) {
            throw new CustomChangeException("Filling CustomFieldType keys failed:", e);
        }

    }
}
