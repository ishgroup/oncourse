package ish.oncourse.test;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class ServiceTest {

	private PageTester tester;

	public void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
		/**
		 * The workaround is used to exclude
		 * "org.apache.commons.dbcp.SQLNestedException:
		 * Cannot create JDBC driver of class 'org.apache.derby.jdbc.EmbeddedDriver'
		 * or connect URL 'jdbc:derby:memory:oncourse;create=true'" exception for
		 * few junits tests which uses embedded jetty server (QEProcessTest fro example)
		 */
		DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
		tester = new PageTester(appPackage, appName, "src/main/webapp", moduleClasses);
		ContextUtils.setupDataSources();
	}
	
	public void initTestWithParams(Map<String, Boolean> params, String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
        tester = new PageTester(appPackage, appName, "src/main/webapp", moduleClasses);
		ContextUtils.setupDataSourcesWithParams(params);
	}
	
	public void initTest(String appPackage, String appName, String contextPath, Class<?>... moduleClasses) throws Exception {
		ContextUtils.setupDataSources();
		tester = new PageTester(appPackage, appName, contextPath, moduleClasses);
	}

    protected <T> T getService(String serviceId, Class<T> serviceInterface) {
        return tester.getRegistry().getService(serviceId, serviceInterface);
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

	public static DataSource getDataSource(String location) throws Exception {
		return ContextUtils.getDataSource(location);
	}

	public static void cleanDataSources() throws Exception {
        BasicDataSource dataSource = (BasicDataSource)getDataSource("jdbc/oncourse");
        dataSource.close();
        BasicDataSource deleteDbDataSourse = (BasicDataSource)ContextUtils.createDeleteDataSource("oncourse");
        try {
            deleteDbDataSourse.getConnection();
        } catch (SQLException e) {
            //ignore
        }
        deleteDbDataSourse.close();
	}

	@After
	public void cleanup() throws Exception {
		cleanDataSources();

		if (tester != null && tester.getRegistry() != null) {
			tester.getRegistry().shutdown();
		}
    }
}
