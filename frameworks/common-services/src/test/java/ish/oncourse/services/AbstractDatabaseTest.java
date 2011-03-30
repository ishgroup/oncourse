package ish.oncourse.services;

import ish.oncourse.test.context.ContextUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.test.PageTesterModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.junit.AfterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDatabaseTest {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDatabaseTest.class);

	private static Registry registry;

	protected static void initTest(String appPackage, String appName, Class<?>... moduleClasses) throws Exception {

		// initialize tapestry service registry
		assert InternalUtils.isNonBlank(appPackage);

		SymbolProvider provider = new SingleKeySymbolProvider(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, appPackage);

		TapestryAppInitializer initializer = new TapestryAppInitializer(logger, provider, appName, PageTesterModule.TEST_MODE,
				null);

		initializer.addModules(PageTesterModule.class);
		initializer.addModules(moduleClasses);

		registry = initializer.createRegistry();
		
		ContextUtils.setupDataSources();
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

	@AfterClass
	public static void cleanup() throws Exception {
		ContextUtils.cleanUpContext();
		registry.shutdown();
	}
}
