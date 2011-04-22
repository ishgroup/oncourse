package ish.oncourse.test;

import javax.sql.DataSource;

import org.apache.tapestry5.test.PageTester;
import org.junit.After;

public class ServiceTest {
	
	private static PageTester tester; 

	public static void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
		tester = new PageTester(appPackage, appName, PageTester.DEFAULT_CONTEXT_PATH, moduleClasses);
	}

	protected <T> T getService(Class<T> serviceInterface) {
		return tester.getRegistry().getService(serviceInterface);
	}

	protected static DataSource getDataSource(String location) throws Exception {
		return ContextUtils.getDataSource(location);
	}

	protected static void cleanDataSource(DataSource dataSource) throws Exception {
		DerbyUtils.cleanDatabase(dataSource.getConnection(), false);
	}

	@After
	public void cleanup() throws Exception {
		if (tester.getRegistry() != null) {
			tester.getRegistry().shutdown();
		}
	}
}
