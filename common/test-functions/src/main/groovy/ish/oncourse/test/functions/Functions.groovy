package ish.oncourse.test.functions

import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.test.MariaDB
import ish.oncourse.test.TestInitialContextFactory
import ish.oncourse.util.ContextUtil
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.commons.dbcp2.BasicDataSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.naming.Context
import javax.naming.InitialContext
import javax.sql.DataSource
import java.sql.*

import static ish.oncourse.test.TestInitialContextFactory.bind
import static ish.oncourse.test.TestInitialContextFactory.close

/**
 * Set of useful functions for tests
 */
class Functions {
    private static final Logger logger = LogManager.getLogger()

    static ServerRuntime createRuntime(String cayenneXml = "cayenne-oncourse.xml") {
        ServerRuntime cayenneRuntime = new ServerRuntime(cayenneXml, new WillowCayenneModuleBuilder().build())
        return cayenneRuntime
    }

    static void bindDS(DataSource dataSource) {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName())
        bind("java:comp/env", new InitialContext())
        bind("jdbc/oncourse", dataSource)
        bind("java:comp/env/jdbc/oncourse", dataSource)
        bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE)
    }

    static void unbindDS() {
        System.properties.remove(Context.INITIAL_CONTEXT_FACTORY)
        close()
    }

    static BasicDataSource createDS(MariaDB mariaDB) {
        BasicDataSource dataSource = new BasicDataSource()
        dataSource.setDriverClassName(mariaDB.driver)
        dataSource.setUrl(mariaDB.url)
        dataSource.setUsername(mariaDB.user)
        dataSource.setPassword(mariaDB.password)
        dataSource.setMaxTotal(100)
        return dataSource
    }


    static void cleanDB(MariaDB mariaDB, boolean drop = true) {
        TimeLog timeLog = new TimeLog()

        Connection connection = null
        PreparedStatement preparedStatement = null
        Statement statement = null
        ResultSet resultSet = null
        try {
            connection = DriverManager.getConnection(mariaDB.url, mariaDB.user, mariaDB.password)
            preparedStatement = connection.prepareStatement(String.format("select Concat(table_schema,'.',TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES where  table_schema = '%s';", mariaDB.dbName))
            resultSet = preparedStatement.executeQuery()
            statement = connection.createStatement();
            statement.addBatch("SET FOREIGN_KEY_CHECKS=0;")
            while (resultSet.next()) {
                statement.addBatch(String.format(drop ? "DROP TABLE %s;" : "TRUNCATE TABLE %s;", resultSet.getString(1)))
            }
            resultSet.close()
            statement.addBatch("SET FOREIGN_KEY_CHECKS=1;")
            statement.executeBatch()
            connection.commit()
            timeLog.log(logger, "Functions.cleanDB timing:")
        } finally {
            if (resultSet != null) {
                resultSet.close()
            }
            if (statement != null) {
                statement.close()
            }
            if (preparedStatement != null) {
                preparedStatement.close()
            }
            if (connection != null) {
                connection.close()
            }
        }
    }

    static void changePassword(MariaDB mariaDB, String newPassword) {
        executeStatement(mariaDB,  "ALTER USER 'root'@'localhost' IDENTIFIED BY '${newPassword}'")
    }

    static void createIfNotExistsDB(MariaDB mariaDB) {
        executeStatement(mariaDB, "CREATE DATABASE IF NOT EXISTS $mariaDB.dbName CHARACTER SET = 'utf8' COLLATE = 'utf8_general_ci'")
    }

    private static void executeStatement(MariaDB mariaDB, String sql) {
        Connection c = null
        PreparedStatement s = null
        try {
            String url = mariaDB.url.replaceAll(mariaDB.dbName, "test")
            c = DriverManager.getConnection(url, mariaDB.user, mariaDB.password)
            s = c.prepareStatement(sql)
            s.executeUpdate()
            c.commit()
        } finally {
            s.close()
            c.close()
        }
    }

    static void setForeignKeyChecks(boolean value, MariaDB mariaDB = MariaDB.valueOf()) {
        Connection connection = null
        PreparedStatement call = null
        try {
            connection = DriverManager.getConnection(mariaDB.url, mariaDB.user, mariaDB.password)
            call = connection.prepareStatement(value ? "SET FOREIGN_KEY_CHECKS=1;" : "SET FOREIGN_KEY_CHECKS=0;")
            call.execute()
            connection.commit()
        } finally {
            if (call != null)
                call.close()
            if (connection != null)
                connection.close()
        }
    }

    static void main(String[] args) {

        MariaDB mariaDB = new MariaDB()
        mariaDB.dbName = "willowTest_services"
        mariaDB.url = "jdbc:mariadb://127.0.0.1:3310/willowTest_services?autoReconnect=true" +
            "&zeroDateTimeBehavior=convertToNull" +
            "&useUnicode=true" +
            "&characterEncoding=utf8"

        createIfNotExistsDB(mariaDB)

//        new TestContext().shouldCreateTables(true).open()
    }


    static class TimeLog {
        private long start = System.currentTimeMillis()

        void log(Logger logger, String message) {
            logger.warn("$message: ${System.currentTimeMillis() - start}")
            start = System.currentTimeMillis()
        }
    }
}

