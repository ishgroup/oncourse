package ish.oncourse.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.test.PageTesterModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.junit.AfterClass;
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
		Context context = new InitialContext();
		DataSource dataSource;
		try {
			Context envContext = (Context) context.lookup("java:comp/env");
			dataSource = (DataSource) envContext.lookup(location);
		} catch (NamingException namingEx) {
			dataSource = (DataSource) context.lookup(location);
		}

		return dataSource;
	}

	protected static void cleanDataSource(DataSource dataSource) throws Exception {
		DerbyUtils.cleanDatabase(dataSource.getConnection(), false);
	}

	@AfterClass
	public static void cleanup() throws Exception {
		if (registry != null) {
			registry.shutdown();
		}
	}
}
