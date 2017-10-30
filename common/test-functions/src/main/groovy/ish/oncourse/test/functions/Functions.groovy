package ish.oncourse.test.functions

import ish.math.MoneyType
import ish.oncourse.test.InitialContextFactoryMock
import ish.oncourse.test.MariaDB
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.commons.dbcp2.BasicDataSource

import javax.naming.Context
import javax.naming.InitialContext
import javax.sql.DataSource
import java.sql.*

/**
 * Set of useful functions for tests
 */
class Functions {

    static ServerRuntime createRuntime(String cayenneXml = "cayenne-oncourse.xml") {
        ServerRuntime cayenneRuntime = new ServerRuntime(cayenneXml)

        for (DataNode dataNode : cayenneRuntime.getDataDomain().getDataNodes()) {
            dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType())
        }
        return cayenneRuntime
    }

    static void initJNDI(DataSource dataSource) {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName())
        InitialContextFactoryMock.bind("java:comp/env", new InitialContext())

        InitialContextFactoryMock.bind("jdbc/oncourse", dataSource)
        InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", dataSource)
    }

    static BasicDataSource createDS(MariaDB mariaDB) {
        DriverManager.registerDriver(Class.forName(mariaDB.driver) as Driver)
        BasicDataSource dataSource = new BasicDataSource()
        dataSource.setDriverClassName(mariaDB.driver)
        dataSource.setUrl(mariaDB.url)
        dataSource.setUsername(mariaDB.user)
        dataSource.setPassword(mariaDB.password)
        dataSource.setMaxTotal(100)
        return dataSource
    }


    static void cleanDB(MariaDB mariaDB, boolean drop = true) {
        Connection connection = null
        PreparedStatement preparedStatement = null
        Statement statement = null
        try {
            connection = DriverManager.getConnection(mariaDB.url, mariaDB.user, mariaDB.password)
            preparedStatement = connection.prepareStatement(String.format("select Concat(table_schema,'.',TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES where  table_schema = '%s';", mariaDB.dbName))
            ResultSet resultSet = preparedStatement.executeQuery()
            statement = connection.createStatement();
            statement.addBatch("SET FOREIGN_KEY_CHECKS=0;")
            while (resultSet.next()) {
                statement.addBatch(String.format(drop ? "DROP TABLE %s;" : "TRUNCATE TABLE %s;", resultSet.getString(1)))
            }
            statement.addBatch("SET FOREIGN_KEY_CHECKS=1;")
            statement.executeBatch()
        } finally {
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
}

