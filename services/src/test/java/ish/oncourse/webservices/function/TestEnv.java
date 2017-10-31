/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.bootique.BQRuntime;
import io.bootique.jdbc.DataSourceFactory;
import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.payment.*;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.MariaDB;
import ish.oncourse.test.TestContext;
import ish.oncourse.webservices.ServicesApp;
import ish.oncourse.webservices.ServicesModule;
import ish.oncourse.webservices.soap.TestConstants;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.test.PageTester;
import org.eclipse.jetty.server.Server;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static ish.oncourse.webservices.ServicesTapestryFilter.SERVICES_APP_PACKAGE;
import static ish.oncourse.webservices.soap.TestConstants.*;
import static ish.oncourse.webservices.usi.TestUSIServiceEndpoint.USI_TEST_MODE;
import static org.junit.Assert.*;
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
	private TestContext testContext;
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

		MariaDB mariaDB = MariaDB.valueOf();

		System.setProperty(Configuration.JDBC_URL_PROPERTY, mariaDB.getUrl());
		System.setProperty(Configuration.AppProperty.DB_USER.getSystemProperty(), mariaDB.getUser());
		System.setProperty(Configuration.AppProperty.DB_PASS.getSystemProperty(), mariaDB.getPassword());


		testContext = new TestContext().open();

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
		testContext.close();
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

	public final void successProcessing(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = pageTester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);

		Element paymentForm = doc.getElementById("paymentDetailsForm");
		assertNotNull("Payment form should be visible ", paymentForm);
		Element cardName = paymentForm.getElementById("cardName");
		assertNotNull("Card name input should be available", cardName);
		Element cardNumber = paymentForm.getElementById("cardNumber");
		assertNotNull("Card number input should be available", cardNumber);
		Element expirityMonth = paymentForm.getElementById("expiryMonth");
		assertNotNull("Card expirity month input should be available", expirityMonth);
		Element expirityYear = paymentForm.getElementById("expiryYear");
		assertNotNull("Card expirity year input should be available", expirityYear);
		Element cardCVV = paymentForm.getElementById("cardCvv");
		assertNotNull("Card CVV input should be available", cardCVV);
		Element submitButton = paymentForm.getElementById("pay-button");
		assertNotNull("Payment form submit should be available", submitButton);

		Map<String, String> fieldValues = new HashMap<>();
		fieldValues.put(cardName.getAttribute(ID_ATTRIBUTE), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute(ID_ATTRIBUTE), VALID_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute(ID_ATTRIBUTE), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute(ID_ATTRIBUTE), VALID_EXPIRY_MONTH);
		fieldValues.put(expirityYear.getAttribute(ID_ATTRIBUTE), VALID_EXPIRY_YEAR);
		fieldValues.put("cardTypeField", CreditCardType.VISA.getDisplayName());


		PaymentRequest paymentRequest = new PaymentRequest();
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentRequest.setAction(PaymentAction.MAKE_PAYMENT);
		paymentRequest.setNumber(VALID_CARD_NUMBER);
		paymentRequest.setName(CARD_HOLDER_NAME);
		paymentRequest.setCvv(CREDIT_CARD_CVV);
		paymentRequest.setMonth(VALID_EXPIRY_MONTH);
		paymentRequest.setYear(VALID_EXPIRY_YEAR);

		getPaymentProcessController(sessionId).processRequest(paymentRequest, paymentResponse);
		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.SUCCESS, paymentResponse.getStatus());
		doc = pageTester.renderPage("Payment/" + sessionId);
		//parse the response result
		Element paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		System.out.println(paymentResultDiv);
		Element successPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-success");
		assertNotNull("Success payment div should be loaded", successPaymentDiv);
		System.out.println(successPaymentDiv);
		Element output = successPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Success payment output should be loaded", output);
		//System.out.println(output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		Node successMessage = output.getChildren().get(0);
		assertNotNull("Success message should be included", successMessage);
		//System.out.println(successMessage);
		assertEquals("Unexpected message", "Payment was successful.", successMessage.toString());
	}

	public final void failedProcessing(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = pageTester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);

		Element paymentForm = doc.getElementById("paymentDetailsForm");
		assertNotNull("Payment form should be visible ", paymentForm);
		Element cardName = paymentForm.getElementById("cardName");
		assertNotNull("Card name input should be available", cardName);
		Element cardNumber = paymentForm.getElementById("cardNumber");
		assertNotNull("Card number input should be available", cardNumber);
		Element expirityMonth = paymentForm.getElementById("expiryMonth");
		assertNotNull("Card expirity month input should be available", expirityMonth);
		Element expirityYear = paymentForm.getElementById("expiryYear");
		assertNotNull("Card expirity year input should be available", expirityYear);
		Element cardCVV = paymentForm.getElementById("cardCvv");
		assertNotNull("Card CVV input should be available", cardCVV);
		Element submitButton = paymentForm.getElementById("pay-button");
		assertNotNull("Payment form submit should be available", submitButton);

		PaymentRequest paymentRequest = new PaymentRequest();
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentRequest.setAction(PaymentAction.MAKE_PAYMENT);
		paymentRequest.setNumber(TestConstants.DECLINED_CARD_NUMBER);
		paymentRequest.setName(CARD_HOLDER_NAME);
		paymentRequest.setCvv(CREDIT_CARD_CVV);
		paymentRequest.setMonth(VALID_EXPIRY_MONTH);
		paymentRequest.setYear(VALID_EXPIRY_YEAR);


		NewPaymentProcessController controller = getPaymentProcessController(sessionId);

		PaymentIn paymentIn = controller.getModel().getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be CARD_DETAILS_REQUIRED", PaymentStatus.CARD_DETAILS_REQUIRED, paymentIn.getStatus());

		//get session to check that processing in progress
		Session session = pageTester.getService(TestableRequest.class).getSession(false);
		assertNull("Session should be null", session);

		controller.processRequest(paymentRequest, paymentResponse);
		//parse the response result
		doc = pageTester.renderPage("Payment/" + sessionId);
		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER, paymentResponse.getStatus());

		//parse the response result
		Element paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		System.out.println(paymentResultDiv);
		Element failPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-fail");
		assertNotNull("Failed payment div should be loaded", failPaymentDiv);
		Element output = failPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Failed payment output should be loaded", output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		Node failedMessage = output.getChildren().get(0);
		assertNotNull("Failed message should be included", failedMessage);
		assertEquals("Unexpected message", "This payment failed because the card was expired, invalid or does not have sufficient funds.",
				failedMessage.toString());

		//fire keep invoice
		Element paymentResultForm = doc.getRootElement().getElementByAttributeValue("class", "submit-wrapper choose-wrapper");
		assertNotNull("Payment result form should be visible ", paymentResultForm);
		Element keepInvoiceButton = paymentResultForm.getElementById("pay-abandon");
		assertNotNull("Payment result form keep invoice submit should be available", keepInvoiceButton);

		paymentRequest = new PaymentRequest();
		paymentRequest.setAction(PaymentAction.CANCEL);
		paymentResponse = new PaymentResponse();
		getPaymentProcessController(sessionId).processRequest(paymentRequest, paymentResponse);

		doc = pageTester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);

		paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		System.out.println(paymentResultDiv);
		failPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-fail");
		assertNotNull("Failed payment div should be loaded", failPaymentDiv);
		output = failPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Failed payment output should be loaded", output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		failedMessage = output.getChildren().get(0);
		assertNotNull("Failed message should be included", failedMessage);
		assertEquals("Unexpected message", "This payment was cancelled.", failedMessage.toString());

		assertEquals("Controller state should be final", GetPaymentState.PaymentState.FAILED, paymentResponse.getStatus());

	}

	public final void checkQueueBeforeProcessing(ObjectContext context) {
		assertTrue("Queue should be empty before processing", ObjectSelect.query(QueuedRecord.class).select(context).isEmpty());
	}

	public final void checkQueueAfterProcessing(ObjectContext context) {
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);

		//Set up sessionId for Invoice.
		//only Invoices can be added to replication queue
		assertEquals("Invoice is not found in a queue", queuedRecords.size(), QueuedRecord.ENTITY_IDENTIFIER.eq(INVOICE_IDENTIFIER).filterObjects(queuedRecords).size());
	}

	public final void checkNotProcessedResponse(GenericTransactionGroup transaction) {
		assertTrue("Get status call should return empty response for in transaction payment",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
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
