/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.bootique.BQRuntime;
import io.bootique.jdbc.DataSourceFactory;
import ish.oncourse.services.payment.NewPaymentProcessController;
import ish.oncourse.services.payment.PaymentControllerBuilder;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.ServicesApp;
import ish.oncourse.webservices.ServicesModule;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.test.PageTester;
import org.eclipse.jetty.server.Server;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static ish.oncourse.webservices.ServicesTapestryFilter.SERVICES_APP_PACKAGE;
import static ish.oncourse.webservices.usi.TestUSIServiceEndpoint.USI_TEST_MODE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: akoiro
 * Date: 26/8/17
 */
public class TestEnv<T extends TransportConfig> {
	//input
	private Function<TestEnv<T>, T> transportConfigProvider;
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);


	//initialized
	private T transportConfig;
	private SupportedVersions supportedVersion;
	private LoadDataSet loadDataSet;
	private BQRuntime runtime;
	private Server server;
	private static ThreadLocal<DataSourceFactory> dataSourceFactory = new ThreadLocal<>();
	private static ThreadLocal<ServerRuntime> serverRuntime = new ThreadLocal<>();
	private PageTester pageTester;
	private org.apache.tapestry5.ioc.Messages messages;
	private ICayenneService cayenneService;

	private Executor executor = Executors.newSingleThreadExecutor();

	public TestEnv(Function<TestEnv<T>, T> transportConfigProvider,
				   String dataSetFile,
				   Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements;
		this.transportConfigProvider = transportConfigProvider;
	}

	public void start() {
		init();

		new CreateTables(serverRuntime.get(), null).create();
		loadDataSet.load(getDataSource());

		startRealServer();

		startTapestry();

		transportConfig = transportConfigProvider.apply(this);
		supportedVersion = transportConfig.getReplicationVersion();
	}

	private void startRealServer() {
		executor.execute(() -> runtime.run());
		while (!server.isStarted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void startTapestry() {
		pageTester = new PageTester(SERVICES_APP_PACKAGE, "app", PageTester.DEFAULT_CONTEXT_PATH, TapestryTestModule.class);
		cayenneService = pageTester.getService(ICayenneService.class);
		messages = mock(Messages.class);
		when(messages.get(anyString())).thenReturn("Any string");
	}

	private void init() {
		System.setProperty(USI_TEST_MODE, Boolean.TRUE.toString());

		loadDataSet = new LoadDataSet(dataSetFile, replacements);
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

	public T getTransportConfig() {
		return transportConfig;
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

	public ICayenneService getCayenneService() {
		return cayenneService;
	}

	public Messages getMessages() {
		return messages;
	}

	public NewPaymentProcessController getPaymentProcessController(String sessionId) {
		return new PaymentControllerBuilder(sessionId,
				getPageTester().getService(INewPaymentGatewayServiceBuilder.class),
				getCayenneService(),
				getMessages(),
				getPageTester().getService(TestableRequest.class)).build();
	}

	public boolean ping() {
		return transportConfig.pingReplicationPort();
	}


	public void authenticate() {
		//firstly ping the server with 10 seconds timeout
		assertTrue("Webservices not ready for call", transportConfig.pingReplicationPort());
		//authenticate
		Long oldCommunicationKey = transportConfig.getCommunicationKey();
		Long newCommunicationKey = transportConfig.authenticate(transportConfig.getSecurityCode(), oldCommunicationKey);
		assertNotNull("Received communication key should not be empty", newCommunicationKey);
		assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
		assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(transportConfig.getCommunicationKey()) == 0);
	}

	public final GenericTransactionGroup processPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		return transportConfig.processPayment(transportConfig.castGenericTransactionGroup(transaction),
				transportConfig.castGenericParametersMap(parametersMap));
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
}
