package ish.oncourse.test.tapestry;

import ish.oncourse.test.TestContext;
import org.apache.cayenne.cache.QueryCache;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.junit.After;

import java.sql.SQLException;

public class ServiceTest {
    protected PageTester tester;
    protected TestContext testContext;

    public void initTest(String appPackage, String appName, QueryCache queryCache, Class<?>... moduleClasses) {
        testContext = new TestContext().queryCache(queryCache).open();
        TestModule.dataSource.set(testContext.getDS());
        TestModule.serverRuntime.set(testContext.getServerRuntime());
        tester = new PageTester(appPackage, appName, "src/main/webapp", moduleClasses);

    }

    public void initTest(String appPackage, String appName, Class<?>... moduleClasses) {
        this.initTest(appPackage, appName, null, moduleClasses);
    }

    public BasicDataSource getDataSource() {
        return testContext.getDS();
    }

    public DatabaseConnection getDatabaseConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(testContext.getDS().getConnection(), null);
    }


    public final <T> T getService(Class<T> serviceInterface) {
        return tester.getRegistry().getService(serviceInterface);
    }

    protected <T> T getObject(Class<T> objectType, AnnotationProvider annotationProvider) {
        return tester.getRegistry().getObject(objectType, annotationProvider);
    }

    public final PageTester getPageTester() {
        return tester;
    }


    @After
    public void cleanup() throws Exception {
        if (tester != null && tester.getRegistry() != null) {
            tester.getRegistry().shutdown();
        }
        if (testContext != null) {
            testContext.close();
        }
    }
}
