/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.bootique.BQRuntime;
import io.bootique.jdbc.DataSourceFactory;
import ish.oncourse.webservices.ServicesApp;
import ish.oncourse.webservices.ServicesModule;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.test.PageTester;
import org.eclipse.jetty.server.Server;

import javax.sql.DataSource;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ish.oncourse.webservices.ServicesTapestryFilter.SERVICES_APP_PACKAGE;

/**
 * User: akoiro
 * Date: 26/8/17
 */
public class TestEnv {
	private SupportedVersions supportedVersion;
	private LoadDataSet loadDataSet;

	private BQRuntime runtime;
	private Server server;
	private static ThreadLocal<DataSourceFactory> dataSourceFactory = new ThreadLocal<>();
	private static ThreadLocal<ServerRuntime> serverRuntime = new ThreadLocal<>();

	private PageTester pageTester;

	private Executor executor = Executors.newSingleThreadExecutor();

	public TestEnv(SupportedVersions supportedVersion, LoadDataSet loadDataSet) {
		this.supportedVersion = supportedVersion;
		this.loadDataSet = loadDataSet;
	}

	public void start() {
		init();
		new CreateTables(serverRuntime.get(), null).create();
		loadDataSet.load(getDataSource());

		executor.execute(() -> runtime.run());
		while (!server.isStarted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		pageTester = new PageTester(SERVICES_APP_PACKAGE, "app", PageTester.DEFAULT_CONTEXT_PATH, TapestryTestModule.class);
	}

	private void init() {
		runtime = ServicesApp.init(new String[]{}).createRuntime();
		dataSourceFactory.set(runtime.getInstance(DataSourceFactory.class));
		serverRuntime.set(runtime.getInstance(ServerRuntime.class));
		server = runtime.getInstance(Server.class);
	}

	public void shutdown() {
		runtime.shutdown();
		pageTester.shutdown();
		new DropDerbyDB("oncourse").drop();
	}

	public URI getURI() {
		return server.getURI();
	}

	public PageTester getPageTester() {
		return pageTester;
	}

	public SupportedVersions getSupportedVersion() {
		return supportedVersion;
	}

	public static class TapestryTestModule {
		public static void bind(ServiceBinder binder) {
			binder.bind(Injector.class, resources -> Guice.createInjector((Module) binder1 -> {
				binder1.bind(ServerRuntime.class).toInstance(
						ServerRuntime.builder()
								.dataSource(dataSourceFactory.get().forName(ServicesModule.DATA_SOURCE_NAME))
								.addConfig("cayenne-oncourse.xml")
								.addModule(new ServicesModule.ServicesCayenneModule()).build()
				);
				binder1.bind(DataSourceFactory.class).toInstance(dataSourceFactory.get());
			}));
		}
	}

	public DataSource getDataSource() {
		return dataSourceFactory.get().forName(ServicesModule.DATA_SOURCE_NAME);
	}

	public GenericParameterEntry createEntry(String name, String value) {
		GenericParameterEntry entry = PortHelper.createParameterEntry(supportedVersion);

		entry.setName(name);
		entry.setValue(value);

		return entry;
	}
}
