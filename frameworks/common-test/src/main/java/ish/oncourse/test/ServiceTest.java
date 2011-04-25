package ish.oncourse.test;


import javax.sql.DataSource;

import org.apache.tapestry5.test.PageTester;
import org.junit.After;

public class ServiceTest {
	
	private PageTester tester; 

	public void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
		tester = new PageTester(appPackage, appName, PageTester.DEFAULT_CONTEXT_PATH, moduleClasses);
		ContextUtils.setupDataSources();
	}

	protected <T> T getService(Class<T> serviceInterface) {
		return tester.getRegistry().getService(serviceInterface);
	}

	public static DataSource getDataSource(String location) throws Exception {
		return ContextUtils.getDataSource(location);
	}

	public static void cleanDataSource(DataSource dataSource) throws Exception {
		DerbyUtils.cleanDatabase(dataSource.getConnection(), false);
	}

	@After
	public void cleanup() throws Exception {
		if (tester!= null && tester.getRegistry() != null) {
			tester.getRegistry().shutdown();
		}
	}
}
