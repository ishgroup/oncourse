/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package test

import groovy.io.FileType
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.mysql.MySqlMetadataHandler
import org.dbunit.operation.DatabaseOperation
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import util.DataSetUtil

import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class PrepareTestDatabase extends DefaultTask {

    @Input
    String databaseUrl

    @Input
    String dataSetPath = ""

    @Input
    String dataSetDirectory = ""

    @TaskAction
    void run() {

        if (!(dataSetPath.isEmpty() ^ dataSetDirectory.isEmpty())) {
            throw new InvalidUserDataException("One of parameters `dataSetPath` or `dataSetDirectory` must be configured.")
        }

        if (!dataSetPath.isEmpty()) {
            String tempDataSetPath = DataSetUtil.createAndGetPathOfTempDatasetBasedOn(dataSetPath)
            importDataSet(tempDataSetPath, databaseUrl)
            return
        }

        loadJdbcDriver()

        loadFromDirectory(dataSetDirectory)
    }


    private void loadFromDirectory(String  dataSetDirectory) {

        if (new File(dataSetDirectory + "/common").exists() && new File(dataSetDirectory + "/common").isDirectory()) {
            loadFromDirectory(dataSetDirectory + "/common")
        }

        new File(dataSetDirectory).eachFile(FileType.FILES) {
            if (FilenameUtils.getExtension(it.path) == 'xml') {
                String tempDataSetPath = DataSetUtil.createAndGetPathOfTempDatasetBasedOn(it.path)
                importDataSet(tempDataSetPath, databaseUrl)
            }
        }

        new File(dataSetDirectory).eachFile(FileType.DIRECTORIES) { directory ->
            if (FilenameUtils.getBaseName(directory.path) != 'common')
            loadFromDirectory(directory.getAbsolutePath())
        }
    }

    private static void importDataSet(String dataSetPath, String databaseUrl) {
        try {
            Connection connection = DriverManager.getConnection(databaseUrl)
            IDatabaseConnection testDatabaseConnection = getDatabaseConnection(connection)
            FlatXmlDataSet dataSet = getDataset(dataSetPath)
            DatabaseOperation.REFRESH.execute(testDatabaseConnection, dataSet)
            updateSequenceSupportTable(connection)
        } finally {
            new File(dataSetPath).delete()
        }
    }

    private static IDatabaseConnection getDatabaseConnection(Connection connection) {
        IDatabaseConnection testDatabaseConnection = new DatabaseConnection(connection, null)
        testDatabaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
        testDatabaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false)
        testDatabaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler())
        return testDatabaseConnection
    }

    private static FlatXmlDataSet getDataset(String dataSetPath) {
        InputStream st = new FileInputStream(dataSetPath)
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setCaseSensitiveTableNames(false).setColumnSensing(true).build(st)
        return dataSet
    }

    private static void updateSequenceSupportTable (Connection connection) {

        connection.createStatement().with { statement ->
            try {
                statement.execute("UPDATE `SequenceSupport` SET `nextId` =  (SELECT COALESCE(max(`invoiceNumber`),0) + 1 FROM `Invoice`) WHERE `tableName` = 'Invoice'")
                statement.execute("UPDATE `SequenceSupport` " +
                        "set `nextId` =  (SELECT COALESCE(max(`studentNumber`),0) + 1 FROM `Student`) " +
                        "WHERE `tableName` = 'student'")
            } finally {
                statement.close()
            }
        }
    }

    // sometimes mariadb jdbc driver doesn't load to classLoader
    private static void loadJdbcDriver() {
        Class.forName("org.mariadb.jdbc.Driver")
    }
}
