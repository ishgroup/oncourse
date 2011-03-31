package ish.oncourse.test;

import javax.sql.DataSource;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.test.PageTesterModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);

	private static Registry registry;

	public static void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {

		SymbolProvider provider = new SingleKeySymbolProvider(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, appPackage);

		TapestryAppInitializer initializer = new TapestryAppInitializer(logger, provider, appName, PageTesterModule.TEST_MODE, null);

		initializer.addModules(moduleClasses);

		registry = initializer.createRegistry();
	}

	protected <T> T getService(Class<T> serviceInterface) {
		return registry.getService(serviceInterface);
	}

	protected static DataSource getDataSource(String location) throws Exception {
		return ContextUtils.getDataSource(location);
	}

	protected static void cleanDataSource(DataSource dataSource) throws Exception {
		DerbyUtils.cleanDatabase(dataSource.getConnection(), false);
	}

	@After
	public void cleanup() throws Exception {
		if (registry != null) {
			registry.shutdown();
		}
	}
}
