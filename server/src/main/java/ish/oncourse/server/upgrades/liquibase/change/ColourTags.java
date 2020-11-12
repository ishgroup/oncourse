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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ColourTags extends IshTaskChange {

    //SQL copes with NULL parentId by just making the result NULL which is sorted to the top
    private static final String SELECT_STATEMENT = "SELECT id FROM Node ORDER BY (parentId*1000+weight)";
    private static final String UPDATE_STATEMENT = "UPDATE Node SET colour=? WHERE id=?";

    private Map<Long, String> records = new HashMap<>();

    @Override
    public void execute(Database database) throws CustomChangeException {
        try {
            var connection = (JdbcConnection) database.getConnection();
            var coloursFile = ColourTags.class.getClassLoader().getResourceAsStream("resources/liquibase/update/release-9.13/tagColours.txt");

            var colourList = new ArrayList<String>();

            try (var br = new BufferedReader(new InputStreamReader(coloursFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    colourList.add(line);
                }
            } catch (Exception e) {
                throw new CustomChangeException("Could not retrieve colours from file.", e);
            }

            //find records
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(SELECT_STATEMENT);
            var updateStatement = connection.prepareStatement(UPDATE_STATEMENT);

            var colourIterator = 0;
            while(resultSet.next()) {

                // endlessly loop over list of hex values
                if (colourIterator == colourList.size()) {
                    colourIterator = 0;
                }
                updateStatement.setString(1, colourList.get(colourIterator));
                updateStatement.setLong(2, resultSet.getLong("id"));
                updateStatement.executeUpdate();
                colourIterator++;
            }
            updateStatement.close();
            resultSet.close();

        } catch (Exception e) {
            System.out.println("colours failed");
            System.out.println(e.getMessage());
            throw new CustomChangeException("Filling tag colours failed: ", e);
        }

    }
}
