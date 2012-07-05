package ish.oncourse.test;

import javax.sql.DataSource;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;

public class ServiceTest {

	private PageTester tester;

	public void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
		tester = new PageTester(appPackage, appName, "src/main/webapp", moduleClasses);
		ContextUtils.setupDataSources();
	}
	
	public void initTest(String appPackage, String appName, String contextPath, Class<?>... moduleClasses) throws Exception {
		ContextUtils.setupDataSources();
		tester = new PageTester(appPackage, appName, contextPath, moduleClasses);
	}

	protected <T> T getService(Class<T> serviceInterface) {
		return tester.getRegistry().getService(serviceInterface);
	}
	
	protected <T> T getObject(Class<T> objectType, AnnotationProvider annotationProvider) {
		return tester.getRegistry().getObject(objectType, annotationProvider);
	}
	
	protected PageTester getPageTester() {
		return tester;
	}

	public static DataSource getDataSource(String location) throws Exception {
		return ContextUtils.getDataSource(location);
	}

	public static void cleanDataSource(DataSource dataSource) throws Exception {
		DerbyUtils.cleanDatabase(dataSource.getConnection(), false);
	}

	public static void cleanDataSources() throws Exception {
		//cleanDataSource(getDataSource("jdbc/oncourse_reference"));
		cleanDataSource(getDataSource("jdbc/oncourse_binary"));
		cleanDataSource(getDataSource("jdbc/oncourse"));

	}

	@After
	public void cleanup() throws Exception {
		cleanDataSources();

		if (tester != null && tester.getRegistry() != null) {
			tester.getRegistry().shutdown();
		}
	}
}
