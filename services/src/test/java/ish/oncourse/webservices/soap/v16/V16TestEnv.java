/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v16;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.payment.*;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.function.LoadDataSet;
import ish.oncourse.webservices.function.TestEnv;
import ish.oncourse.webservices.soap.TestConstants;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v16.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v16.stubs.replication.TransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.test.PageTester;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static ish.oncourse.webservices.soap.TestConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class V16TestEnv {
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
	private TestEnv testEnv;
	private V16TransportConfig transportConfig;
	private ICayenneService cayenneService;
	private org.apache.tapestry5.ioc.Messages messages;


	public V16TestEnv(String dataSetFile, Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements == null ? this.replacements : replacements;
	}

	public V16TestEnv start() {
		testEnv = new TestEnv(SupportedVersions.V16,
				new LoadDataSet(dataSetFile, replacements));
		testEnv.start();
		cayenneService = testEnv.getPageTester().getService(ICayenneService.class);

		transportConfig = new V16TransportConfig(testEnv);
		transportConfig.init();
		transportConfig.pingReplicationPort();
		transportConfig.pingPaymentPort();
		transportConfig.pingReferencePort();

		messages = mock(org.apache.tapestry5.ioc.Messages.class);
		when(messages.get(anyString())).thenReturn("Any string");
		return this;

	}


	public void shutdown() {
		testEnv.shutdown();
	}

	public boolean ping() {
		return transportConfig.pingReplicationPort();
	}

	public void authenticate() {
		try {
			//firstly ping the server with 10 seconds timeout
			assertTrue("Webservices not ready for call", transportConfig.pingReplicationPort());
			//authenticate
			Long oldCommunicationKey = transportConfig.getCommunicationKey();
			Long newCommunicationKey = getReplicationPortType().authenticate(transportConfig.getSecurityCode(), oldCommunicationKey);
			assertNotNull("Received communication key should not be empty", newCommunicationKey);
			assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
			assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(transportConfig.getCommunicationKey()) == 0);
		} catch (AuthFailure authFailure) {
			throw new RuntimeException(authFailure);
		}
	}


	protected TransactionGroup castGenericTransactionGroup(GenericTransactionGroup transaction) {
		return (TransactionGroup) transaction;
	}

	protected ParametersMap castGenericParametersMap(GenericParametersMap parametersMap) {
		return (ParametersMap) parametersMap;
	}

	public ReplicationPortType getReplicationPortType() {
		return transportConfig.getReplicationPortType();
	}

	public PaymentPortType getPaymentPortType() {
		return transportConfig.getPaymentPortType();
	}

	public V16TransportConfig getTransportConfig() {
		return transportConfig;
	}

	public TestEnv getTestEnv() {
		return testEnv;
	}

	public ICayenneService getCayenneService() {
		return cayenneService;
	}

	public Messages getMessages() {
		return messages;
	}

	public PageTester getPageTester() {
		return testEnv.getPageTester();
	}

	public SupportedVersions getSupportedVersion() {
		return testEnv.getSupportedVersion();
	}


	public NewPaymentProcessController getController(String sessionId) {
		return new PaymentControllerBuilder(sessionId,
				getPageTester().getService(INewPaymentGatewayServiceBuilder.class),
				getCayenneService(),
				getMessages(),
				getPageTester().getService(TestableRequest.class)).build();
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

	public final void renderPaymentPageWithKeepInvoiceProcessing(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = testEnv.getPageTester().renderPage("Payment/" + sessionId);
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


		NewPaymentProcessController controller = getController(sessionId);

		PaymentIn paymentIn = controller.getModel().getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be CARD_DETAILS_REQUIRED", PaymentStatus.CARD_DETAILS_REQUIRED, paymentIn.getStatus());

		//get session to check that processing in progress
		Session session = testEnv.getPageTester().getService(TestableRequest.class).getSession(false);
		assertNull("Session should be null", session);

		controller.processRequest(paymentRequest, paymentResponse);
		//parse the response result
		doc = testEnv.getPageTester().renderPage("Payment/" + sessionId);
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
		getController(sessionId).processRequest(paymentRequest, paymentResponse);

		doc = testEnv.getPageTester().renderPage("Payment/" + sessionId);
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

	public final GenericTransactionGroup getPaymentStatus(String sessionId) {
		try {
			return getPaymentPortType().getPaymentStatus(sessionId);
		} catch (ReplicationFault replicationFault) {
			throw new RuntimeException(replicationFault);
		}
	}

	public final GenericTransactionGroup processPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		try {
			return getPaymentPortType().processPayment(castGenericTransactionGroup(transaction),
					castGenericParametersMap(parametersMap));
		} catch (ReplicationFault replicationFault) {
			throw new RuntimeException(replicationFault);
		}
	}


	public static class TestCase {
		private V16TestEnv testEnv;
		private BiConsumer<GenericTransactionGroup, GenericParametersMap> fillStubs;
		private Function<GenericTransactionGroup, String> getSessionId;
		private Consumer<ObjectContext> assertQueuedRecords;
		private Consumer<GenericTransactionGroup> assertResponse;
		private Consumer<String> processPayment;

		public TestCase(V16TestEnv testEnv, BiConsumer<GenericTransactionGroup, GenericParametersMap> fillStubs,
						Function<GenericTransactionGroup, String> getSessionId,
						Consumer<String> processPayment,
						Consumer<ObjectContext> assertQueuedRecords,
						Consumer<GenericTransactionGroup> assertResponse) {
			super();
			this.testEnv = testEnv;
			this.fillStubs = fillStubs;
			this.getSessionId = getSessionId;
			this.assertQueuedRecords = assertQueuedRecords;
			this.assertResponse = assertResponse;
			this.processPayment = processPayment;
		}

		public void test() {
			//check that empty queuedRecords
			ObjectContext context = testEnv.getCayenneService().newNonReplicatingContext();
			testEnv.checkQueueBeforeProcessing(context);
			testEnv.authenticate();
			// prepare the stubs for replication
			GenericTransactionGroup transaction = PortHelper.createTransactionGroup(testEnv.getSupportedVersion());
			GenericParametersMap parametersMap = PortHelper.createParametersMap(testEnv.getSupportedVersion());
			fillStubs.accept(transaction, parametersMap);
			//process payment
			transaction = testEnv.processPayment(transaction, parametersMap);
			//check the response, validate the data and receive the sessionid
			String sessionId = getSessionId.apply(transaction);
			testEnv.checkQueueAfterProcessing(context);
			//check the status via service
			testEnv.checkNotProcessedResponse(testEnv.getPaymentStatus(sessionId));
			//call page processing
			processPayment.accept(sessionId);
			//check that async replication works correct
			assertQueuedRecords.accept(context);
			//check the status via service when processing complete
			assertResponse.accept(testEnv.getPaymentStatus(sessionId));

		}
	}
}
