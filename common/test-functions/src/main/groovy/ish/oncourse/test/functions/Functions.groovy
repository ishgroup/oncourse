package ish.oncourse.test.functions

import ish.math.MoneyType
import ish.oncourse.test.InitialContextFactoryMock
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.configuration.CayenneRuntime
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.commons.dbcp2.BasicDataSource

import javax.naming.Context
import javax.naming.InitialContext
import javax.sql.DataSource

/**
 * Set of useful functions for tests
 */
class Functions {

    static Closure<CayenneRuntime> cayenneRuntime = { String cayenneXml = "cayenne-oncourse.xml", Closure initJNDI = initJNDI ->
        initJNDI()

        ServerRuntime cayenneRuntime = new ServerRuntime(cayenneXml)

        for (DataNode dataNode : cayenneRuntime.getDataDomain().getDataNodes()) {
            dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType())
        }
        return cayenneRuntime
    }


    static Closure initJNDI = { Closure<DataSource> dataSource = createDataSource ->
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName())
        InitialContextFactoryMock.bind("java:comp/env", new InitialContext())

        DataSource oncourse = dataSource()
        InitialContextFactoryMock.bind("jdbc/oncourse", oncourse)
        InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse)
    }

    static Closure<DataSource> createDataSource = { String driverClass = 'org.gjt.mm.mysql.Driver',
                                             String url = 'jdbc:mysql://127.0.0.1:3306/w2test_college?useSSL=false&serverTimezone=Australia/Sydney',
                                             String userName = 'root',
                                             String password = 'whatsup' ->
        BasicDataSource dataSource = new BasicDataSource()
        dataSource.setDriverClassName(driverClass)
        dataSource.setUrl(url)
        dataSource.setUsername(userName)
        dataSource.setPassword(password)
        dataSource.setMaxTotal(1)
        return dataSource
    }
}
