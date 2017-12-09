package ish.oncourse.test;

import ish.oncourse.services.cache.NoopQueryCache;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;

import javax.sql.DataSource;

public class ServiceTest {
	public static ThreadLocal<DataSource> dataSource = new ThreadLocal<>();
	public static ThreadLocal<ServerRuntime> serverRuntime = new ThreadLocal<>();

	protected PageTester tester;
	protected TestContext testContext;

	public void initTest(String appPackage, String appName, Class<?>... moduleClasses) {
		testContext = new TestContext().queryCache(new NoopQueryCache()).open();
		dataSource.set(testContext.getDS());
		serverRuntime.set(testContext.getServerRuntime());
		tester = new PageTester(appPackage, appName, "src/main/webapp", moduleClasses);
	}

	public BasicDataSource getDataSource(String jndiName) {
		return testContext.getDS();
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
	}
}
