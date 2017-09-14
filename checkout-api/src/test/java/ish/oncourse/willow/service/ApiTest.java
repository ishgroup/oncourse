/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.willow.service;


import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import ish.math.MoneyType;
import ish.oncourse.willow.WillowApiModule;
import ish.oncourse.willow.cayenne.CayenneService;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;

public abstract class ApiTest {
    private static Mysql mysql;
    private static boolean createSchema = false;
    private static boolean dropSchema = false;
    private static boolean createTables = false;
    private static DataSource dataSource;

    protected ServerRuntime cayenneRuntime;
    protected CayenneService cayenneService;

    static {
        
    }
    
    @BeforeClass
    public static void initTest() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
        InitialContextFactoryMock.bind("java:comp/env", new InitialContext());
        dataSource = createDataSource();
        
        InitialContextFactoryMock.bind("jdbc/oncourse", dataSource);
        InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", dataSource);
    }

    @Before
    public void setup() throws Exception {
        InputStream st = ApiTest.class.getClassLoader().getResourceAsStream(getDataSetResource());
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        
        DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        truncateAllTables(false);
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        
        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new WillowApiModule.WillowApiCayenneModule());
        for (DataNode dataNode : cayenneRuntime.getDataDomain().getDataNodes()) {
            dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
        }
        cayenneService = new CayenneService(cayenneRuntime);
    }
    
    @After
    public void after() {
        cayenneRuntime.shutdown();
    }    
    
    @AfterClass
    public static void afterClass() throws Exception {
        AbandonedConnectionCleanupThread.checkedShutdown();
        if (dropSchema) {
            dropMysqlSchema();
        }
    }
    
    private static void dropMysqlSchema() throws SQLException {
        Connection connection = DriverManager.getConnection(mysql.mysqlUri);
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("DROP DATABASE %s ;", mysql.dbName));
        preparedStatement.execute();
        connection.close();
    }
    
    
    protected abstract String getDataSetResource();


    private static void initJNDI(DataSource oncourse) {
        try {
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:/comp/env");
            ic.createSubcontext("java:/comp/env/jdbc");

            ic.bind("java:comp/env/jdbc/oncourse", oncourse);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static DataSource createDataSource() throws Exception {
        String jdbcUrl = System.getProperty("oncourse.jdbc.url") + "?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8";
        String jdbcUser = System.getProperty("oncourse.jdbc.user");
        String jdbcPassword = System.getProperty("oncourse.jdbc.password");

        
        String driverClass = com.mysql.jdbc.Driver.class.getName();
        
        mysql = Mysql.valueOf(jdbcUrl, jdbcUser, jdbcPassword);

        truncateAllTables(createTables);

        if (createSchema) {
            createMysqlSchema();
        }

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(mysql.jdbcUrl);
        dataSource.setUsername(mysql.jdbcUser);
        dataSource.setPassword(mysql.jdbcPassword);
        dataSource.setMaxTotal(100);
        return dataSource;
    }


    private static void createMysqlSchema() throws SQLException {
        Connection connection = DriverManager.getConnection(mysql.mysqlUri, mysql.jdbcUser, mysql.jdbcPassword);
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("CREATE SCHEMA %s DEFAULT CHARACTER SET ascii ;", mysql.dbName));
        preparedStatement.execute();
        connection.close();
    }

    private static void truncateAllTables(boolean dropTables) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(mysql.mysqlUri, mysql.jdbcUser, mysql.jdbcPassword);
            preparedStatement = connection.prepareStatement(String.format("select Concat(table_schema,'.',TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES where  table_schema = '%s';", mysql.dbName));
            ResultSet resultSet = preparedStatement.executeQuery();


            statement = connection.createStatement();
            statement.addBatch("SET FOREIGN_KEY_CHECKS=0;");
            while (resultSet.next()) {
                statement.addBatch(String.format(dropTables ? "DROP TABLE %s;" : "TRUNCATE TABLE %s;", resultSet.getString(1)));
            }
            statement.addBatch("SET FOREIGN_KEY_CHECKS=1;");
            statement.executeBatch();
        } finally {

             if (statement != null) {
                statement.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }

    
    static class Mysql {
        String jdbcUrl;
        String jdbcUser;
        String jdbcPassword;
        String mysqlUri;
        String dbName;

        static Mysql valueOf(String jdbcUrl, String jdbcUser, String jdbcPassword) {
            Mysql result = new Mysql();
            result.jdbcUrl = jdbcUrl;
            result.jdbcUser = jdbcUser;
            result.jdbcPassword = jdbcPassword;
            String[] parts = StringUtils.split(jdbcUrl, "?");
            String[] urlParts = StringUtils.split(parts[0], "/");
            result.mysqlUri = urlParts[0] + "//" + urlParts[1] + "?" + parts[1];
            result.dbName = urlParts[2];
            return result;
        }

    }
    
}
