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

package ish.oncourse.api.test.task;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PrepareTestDb {

    private static final int DB_URL_ARG_INDEX = 0;
    private static final int DATASET_PATH_ARG_INDEX = 1;
    
    // Args structure:
    // args[0] - db url
    // args[1] - path to dataset file
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            Connection connection = DriverManager.getConnection(args[DB_URL_ARG_INDEX]);
            IDatabaseConnection testDatabaseConnection = new DatabaseConnection(connection, null);
            testDatabaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            InputStream st = new FileInputStream(args[DATASET_PATH_ARG_INDEX]);
            FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setCaseSensitiveTableNames(false).setColumnSensing(true).build(st);
            DatabaseOperation.REFRESH.execute(testDatabaseConnection, dataSet);

            try(var statement = connection.createStatement()) {
                statement.execute("UPDATE `SequenceSupport` " +
                                        "set `nextId` =  (SELECT max(`invoiceNumber`) + 1 FROM `Invoice`) " +
                                        "WHERE `tableName` = 'invoice'");
                
                statement.execute("UPDATE `SequenceSupport` " +
                        "set `nextId` =  (SELECT max(`studentNumber`) + 1 FROM `Student`) " +
                        "WHERE `tableName` = 'student'");

            }
            
        } else {
            throw new RuntimeException("Test data wasn't inserted. Invalid parameters count!");
        }
    }
}
