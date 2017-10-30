package ish.oncourse.test;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;

import java.util.Map;

public class ServiceTest {

	private PageTester tester;
	protected TestContext testContext;

	public void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
		testContext = new TestContext().init();
		tester = new PageTester(appPackage, appName, "src/main/webapp", moduleClasses);
	}

	public void initTestWithParams(Map<String, Boolean> params, String appPackage, String appName, Class<?>... moduleClasses) throws Exception {
		testContext = new TestContext().params(params).init();
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
