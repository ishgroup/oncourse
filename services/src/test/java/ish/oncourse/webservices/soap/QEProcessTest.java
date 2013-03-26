package ish.oncourse.webservices.soap;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class QEProcessTest extends AbstractTransportTest {
	private static final String ID_ATTRIBUTE = "id";
	private static final String ENROLMENT_IDENTIFIER = Enrolment.class.getSimpleName();
	private static final String INVOICE_LINE_IDENTIFIER = InvoiceLine.class.getSimpleName();
	private static final String PAYMENT_LINE_IDENTIFIER = PaymentInLine.class.getSimpleName();
	private static final String INVOICE_IDENTIFIER = Invoice.class.getSimpleName();
	private static final String PAYMENT_IDENTIFIER = PaymentIn.class.getSimpleName();
	private static final String V4_PAYMENT_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v4/payment";
	private static final String V4_REPLICATION_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v4/replication";
	private static final String V4_REPLICATION_WSDL = "wsdl/v4_replication.wsdl";
	public static final String CARD_HOLDER_NAME = "john smith";
	public static final String VALID_CARD_NUMBER = "5431111111111111";
	public static final String DECLINED_CARD_NUMBER = "9999990000000378";
	public static final String CREDIT_CARD_CVV = "1111";
	
	private static final String VALID_EXPIRITY_MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1 + StringUtils.EMPTY;
	private static final String VALID_EXPIRITY_YEAR = Calendar.getInstance().get(Calendar.YEAR) + StringUtils.EMPTY;


	private ServiceTest serviceTest;
	private PageTester tester;
	private ICayenneService cayenneService;
	
	@BeforeClass
	public static void before() throws Exception {
		startRealWSServer();
	}
	
	private static void startRealWSServer() throws Exception {
		server = new TestServer(9092, TestServer.DEFAULT_CONTEXT_PATH, "src/main/webapp/WEB-INF", TestServer.DEFAULT_HOST, 
			"src/main", TestServer.DEFAULT_WEB_XML_FILE_PATH);
		server.start();
	} 

	@AfterClass
	public static void after() throws Exception {
		System.out.println("stop the server");
		stopServer();
	}
	
	@Before
	public void setup() throws Exception {
		serviceTest = new ServiceTest();
		serviceTest.initTest("ish.oncourse.webservices", "services", PaymentServiceTestModule.class);
		tester = serviceTest.getPageTester();
		InputStream st = QEProcessTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/QEProcessDataset.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource onDataSource = ServiceTest.getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        cayenneService = serviceTest.getService(ICayenneService.class);
	}
	
	/**
	 * Cleanup required to prevent test fail on before database cleanup.
	 * @throws Exception 
	 */
	@After
	public void cleanup() throws Exception {
		System.out.println("cleanup the database");
		serviceTest.cleanup();
	}
	
	void testRenderPaymentPageWithKeepInvoice(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		
		Element paymentForm = doc.getElementById("paymentDetailsForm");
		assertNotNull("Payment form should be visible ", paymentForm);
		Element cardName = paymentForm.getElementById("cardName");
		assertNotNull("Card name input should be available", cardName);
		Element cardNumber = paymentForm.getElementById("cardnumber");
		assertNotNull("Card number input should be available", cardNumber);
		Element expirityMonth = paymentForm.getElementById("expiryMonth");
		assertNotNull("Card expirity month input should be available", expirityMonth);
		Element expirityYear = paymentForm.getElementById("expiryYear");
		assertNotNull("Card expirity year input should be available", expirityYear);
		Element cardCVV = paymentForm.getElementById("cardcvv");
		assertNotNull("Card CVV input should be available", cardCVV);
		Element submitButton = paymentForm.getElementById("paymentSubmit");
		assertNotNull("Payment form submit should be available", submitButton);
		
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put(cardName.getAttribute(ID_ATTRIBUTE), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute(ID_ATTRIBUTE), DECLINED_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute(ID_ATTRIBUTE), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_MONTH);
		fieldValues.put(expirityYear.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_YEAR);
		fieldValues.put("cardTypeField", CreditCardType.VISA.getDisplayName());
		
		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNotNull("Session should be inited for controller", session);
		PaymentProcessController controller = (PaymentProcessController) session.getAttribute("state:Payment::paymentProcessController");
		assertNotNull("controller should be not empty", controller);
		//load the payment structure 
		PaymentIn paymentIn = controller.getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		List<PaymentInLine> paymentInLines = paymentIn.getPaymentInLines();
		assertTrue("PaymentInLines size should be 1", paymentInLines.size() == 1);
		Invoice invoice = paymentInLines.get(0).getInvoice();
		assertNotNull("Invoice should not be empty", invoice);
		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		assertTrue("InvoiceLines size should be 1", invoiceLines.size() == 1);
		List<InvoiceLineDiscount> discounts = invoiceLines.get(0).getInvoiceLineDiscounts();
		assertTrue("No discounts should be applied", discounts.size() == 0);
		Enrolment enrolment = invoiceLines.get(0).getEnrolment();
		assertNotNull("Enrolment should not be empty", enrolment);
		assertEquals("Enrolment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		
		//submit the data
		TestableResponse response = tester.clickSubmitAndReturnResponse(submitButton, fieldValues);
		assertNotNull("response should not be empty", response);
		System.out.println(response.getRedirectURL());
		
		//start wait in loop for response
		while (controller.isProcessingState()) {
			try {
				//sleep for 5 seconds to have some time for processing
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc = tester.renderPage("Payment/" + sessionId);
			assertNotNull("Document should be loaded", doc);
		}
		
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
		Element paymentResultForm = doc.getElementById("paymentResultForm");
		assertNotNull("Payment result form should be visible ", paymentResultForm);
		Element keepInvoiceButton = paymentResultForm.getElementById("abandonAndKeep");
		assertNotNull("Payment result form keep invoice submit should be available", keepInvoiceButton);
		
		//submit the data
		response = tester.clickSubmitAndReturnResponse(keepInvoiceButton, new HashMap<String, String>());
		assertNotNull("response should not be empty", response);
		System.out.println(response.getRedirectURL());
		
		//start wait in loop for response
		while (!controller.isFinalState()) {
			try {
				//sleep for 5 seconds to have some time for processing
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc = tester.renderPage("Payment/" + sessionId);
			assertNotNull("Document should be loaded", doc);
		}
		
		doc = tester.renderPage("Payment/" + sessionId);
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
		
		assertTrue("Controller state should be final", controller.isFinalState());
		
	}
	
	void testRenderPaymentPageForExpiration(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		
		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNotNull("Session should be inited for controller", session);
		PaymentProcessController controller = (PaymentProcessController) session.getAttribute("state:Payment::paymentProcessController");
		assertNotNull("controller should be not empty", controller);
		//load the payment structure till expiration
		PaymentIn paymentIn = controller.getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		List<PaymentInLine> paymentInLines = paymentIn.getPaymentInLines();
		assertTrue("PaymentInLines size should be 1", paymentInLines.size() == 1);
		Invoice invoice = paymentInLines.get(0).getInvoice();
		assertNotNull("Invoice should not be empty", invoice);
		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		assertTrue("InvoiceLines size should be 1", invoiceLines.size() == 1);
		List<InvoiceLineDiscount> discounts = invoiceLines.get(0).getInvoiceLineDiscounts();
		assertTrue("No discounts should be applied", discounts.size() == 0);
		Enrolment enrolment = invoiceLines.get(0).getEnrolment();
		assertNotNull("Enrolment should not be empty", enrolment);
		assertEquals("Enrolment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		//expire the payment
		controller.processAction(PaymentAction.EXPIRE_PAYMENT);
		//reload the page to receive the cancel payment message
		doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		assertTrue("Controller state should be expired", controller.isExpired());
		
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
		assertEquals("Unexpected message", "This payment was cancelled.", failedMessage.toString());
	}
	
	void testRenderPaymentPage(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		
		Element paymentForm = doc.getElementById("paymentDetailsForm");
		assertNotNull("Payment form should be visible ", paymentForm);
		Element cardName = paymentForm.getElementById("cardName");
		assertNotNull("Card name input should be available", cardName);
		Element cardNumber = paymentForm.getElementById("cardnumber");
		assertNotNull("Card number input should be available", cardNumber);
		Element expirityMonth = paymentForm.getElementById("expiryMonth");
		assertNotNull("Card expirity month input should be available", expirityMonth);
		Element expirityYear = paymentForm.getElementById("expiryYear");
		assertNotNull("Card expirity year input should be available", expirityYear);
		Element cardCVV = paymentForm.getElementById("cardcvv");
		assertNotNull("Card CVV input should be available", cardCVV);
		Element submitButton = paymentForm.getElementById("paymentSubmit");
		assertNotNull("Payment form submit should be available", submitButton);
		
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put(cardName.getAttribute(ID_ATTRIBUTE), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute(ID_ATTRIBUTE), VALID_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute(ID_ATTRIBUTE), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_MONTH);
		fieldValues.put(expirityYear.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_YEAR);
		fieldValues.put("cardTypeField", CreditCardType.VISA.getDisplayName());
		
		//submit the data
		TestableResponse response = tester.clickSubmitAndReturnResponse(submitButton, fieldValues);
		assertNotNull("response should not be empty", response);
		System.out.println(response.getRedirectURL());
		
		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNotNull("Session should be inited for controller", session);
		PaymentProcessController controller = (PaymentProcessController) session.getAttribute("state:Payment::paymentProcessController");
		assertNotNull("controller should be not empty", controller);
		
		//start wait in loop for response
		while (controller.isProcessingState()) {
			try {
				//sleep for 5 seconds to have some time for processing
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc = tester.renderPage("Payment/" + sessionId);
			assertNotNull("Document should be loaded", doc);
		}
		
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
	
	//@Test
	public void testQEKeepInvoice() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//authenticate first
		Long oldCommunicationKey = getCommunicationKey();
		Long newCommunicationKey = getReplicationPortType().authenticate(getSecurityCode(), oldCommunicationKey);
		assertNotNull("Received communication key should not be empty", newCommunicationKey);
		assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
		assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(getCommunicationKey()) == 0);
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(SupportedVersions.V4);
		fillV4PaymentStubs(transaction);
		/*//TODO: cleanup me
		Session session = serviceTest.getService(TestableRequest.class).getSession(true);
		session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, 1l);
		InternalPaymentService paymentPort = tester.getService(InternalPaymentService.class);
		transaction = paymentPort.processPayment(transaction);
		//TODO: cleanup me*/
		//process payment
		transaction = getPaymentPortType().processPayment((TransactionGroup) transaction);
		//check the response, validate the data and receive the sessionid
		String sessionId = null;
		assertEquals("11 stubs should be in response for this processing", 11, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertNotNull("Willowid after the first payment processing should not be NULL", stub.getWillowId());
			if (PAYMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub payment = (GenericPaymentInStub) stub;
				assertNull("Only 1 paymentIn entity should exist in response. This entity sessionid will be used for page processing", sessionId);
				sessionId = payment.getSessionId();
				assertNotNull("PaymentIn entity should contain the sessionid after processing", sessionId);
				assertEquals("Payment status should not change after this processing", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), payment.getStatus());
			} else if (ENROLMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.IN_TRANSACTION.name(), 
					((GenericEnrolmentStub) stub).getStatus());
			}
		}
		assertTrue("Queue should be empty after processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		/*//TODO: cleanup me
		transaction = paymentPort.getPaymentStatus(sessionId, SupportedVersions.V4);
		//TODO: cleanup me*/
		//check the status via service
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertTrue("Get status call should return empty response for in transaction payment", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//call page processing
		//TODO: update me
		testRenderPaymentPageWithKeepInvoice(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertTrue("Queue should contain 5 records.", queuedRecords.size() == 5 );
		boolean isPaymentFound = false, isPaymentLineFound = false, isInvoiceFound = false, isInvoiceLineFound = false, isEnrolmentDound = false;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentIn should exist in a queue", isPaymentFound);
				isPaymentFound = true;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentInLine should exist in a queue", isPaymentLineFound);
				isPaymentLineFound = true;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoice should exist in a queue", isInvoiceFound);
				isInvoiceFound = true;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoiceLine should exist in a queue", isInvoiceLineFound);
				isInvoiceLineFound = true;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentDound);
				isEnrolmentDound = true;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertTrue("Payment not found in a queue", isPaymentFound);
		assertTrue("Payment line not found in a queue", isPaymentLineFound);
		assertTrue("Invoice not found in a queue", isInvoiceFound);
		assertTrue("Invoice line not found in a  queue", isInvoiceLineFound);
		assertTrue("Enrolment not found in a queue", isEnrolmentDound);
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertTrue("11 elements should be replicated for this payment", transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size() == 11);
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				assertTrue("Payment status should be failed after expiration", PaymentStatus.FAILED.equals(status));
			}
		}
		//logout
		getReplicationPortType().logout(getCommunicationKey());
	}
	
	//@Test
	public void testExpireQEByWatchdog() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//authenticate first
		Long oldCommunicationKey = getCommunicationKey();
		Long newCommunicationKey = getReplicationPortType().authenticate(getSecurityCode(), oldCommunicationKey);
		assertNotNull("Received communication key should not be empty", newCommunicationKey);
		assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
		assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(getCommunicationKey()) == 0);
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(SupportedVersions.V4);
		fillV4PaymentStubs(transaction);
		/*//TODO: cleanup me
		Session session = serviceTest.getService(TestableRequest.class).getSession(true);
		session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, 1l);
		InternalPaymentService paymentPort = tester.getService(InternalPaymentService.class);
		transaction = paymentPort.processPayment(transaction);
		//TODO: cleanup me*/
		//process payment
		transaction = getPaymentPortType().processPayment((TransactionGroup) transaction);
		//check the response, validate the data and receive the sessionid
		String sessionId = null;
		assertEquals("11 stubs should be in response for this processing", 11, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertNotNull("Willowid after the first payment processing should not be NULL", stub.getWillowId());
			if (PAYMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub payment = (GenericPaymentInStub) stub;
				assertNull("Only 1 paymentIn entity should exist in response. This entity sessionid will be used for page processing", sessionId);
				sessionId = payment.getSessionId();
				assertNotNull("PaymentIn entity should contain the sessionid after processing", sessionId);
				assertEquals("Payment status should not change after this processing", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), payment.getStatus());
			} else if (ENROLMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.IN_TRANSACTION.name(), 
					((GenericEnrolmentStub) stub).getStatus());
			}
		}
		assertTrue("Queue should be empty after processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		/*//TODO: cleanup me
		transaction = paymentPort.getPaymentStatus(sessionId, SupportedVersions.V4);
		//TODO: cleanup me*/
		//check the status via service
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertTrue("Get status call should return empty response for in transaction payment", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//call page processing
		testRenderPaymentPageForExpiration(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertTrue("Queue should contain 5 records.", queuedRecords.size() == 5 );
		boolean isPaymentFound = false, isPaymentLineFound = false, isInvoiceFound = false, isInvoiceLineFound = false, isEnrolmentDound = false;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentIn should exist in a queue", isPaymentFound);
				isPaymentFound = true;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentInLine should exist in a queue", isPaymentLineFound);
				isPaymentLineFound = true;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoice should exist in a queue", isInvoiceFound);
				isInvoiceFound = true;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoiceLine should exist in a queue", isInvoiceLineFound);
				isInvoiceLineFound = true;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentDound);
				isEnrolmentDound = true;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertTrue("Payment not found in a queue", isPaymentFound);
		assertTrue("Payment line not found in a queue", isPaymentLineFound);
		assertTrue("Invoice not found in a queue", isInvoiceFound);
		assertTrue("Invoice line not found in a  queue", isInvoiceLineFound);
		assertTrue("Enrolment not found in a queue", isEnrolmentDound);
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertTrue("11 elements should be replicated for this payment", transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size() == 11);
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				assertTrue("Payment status should be failed after expiration", PaymentStatus.FAILED.equals(status));
			}
		}
		//logout
		getReplicationPortType().logout(getCommunicationKey());
	}
	
	//@Test
	public void testReplicateQEData() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();//TODO: fix me in 17088
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//authenticate first
		Long oldCommunicationKey = getCommunicationKey();
		Long newCommunicationKey = getReplicationPortType().authenticate(getSecurityCode(), oldCommunicationKey);
		assertNotNull("Received communication key should not be empty", newCommunicationKey);
		assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
		assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(getCommunicationKey()) == 0);
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(SupportedVersions.V4);
		fillV4PaymentStubs(transaction);
		//process payment
		transaction = getPaymentPortType().processPayment((TransactionGroup) transaction);
		//check the response, validate the data and receive the sessionid
		String sessionId = null;
		assertEquals("11 stubs should be in response for this processing", 11, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertNotNull("Willowid after the first payment processing should not be NULL", stub.getWillowId());
			if (PAYMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub payment = (GenericPaymentInStub) stub;
				assertNull("Only 1 paymentIn entity should exist in response. This entity sessionid will be used for page processing", sessionId);
				sessionId = payment.getSessionId();
				assertNotNull("PaymentIn entity should contain the sessionid after processing", sessionId);
				assertEquals("Payment status should not change after this processing", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), payment.getStatus());
			} else if (ENROLMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.IN_TRANSACTION.name(), 
					((GenericEnrolmentStub) stub).getStatus());
			}
		}
		assertTrue("Queue should be empty after processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//check the status via service
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertTrue("Get status call should return empty response for in transaction payment", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//call page processing
		testRenderPaymentPage(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertTrue("Queue should contain 5 records.", queuedRecords.size() == 5 );
		boolean isPaymentFound = false, isPaymentLineFound = false, isInvoiceFound = false, isInvoiceLineFound = false, isEnrolmentDound = false;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentIn should exist in a queue", isPaymentFound);
				isPaymentFound = true;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentInLine should exist in a queue", isPaymentLineFound);
				isPaymentLineFound = true;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoice should exist in a queue", isInvoiceFound);
				isInvoiceFound = true;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoiceLine should exist in a queue", isInvoiceLineFound);
				isInvoiceLineFound = true;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentDound);
				isEnrolmentDound = true;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertTrue("Payment not found in a queue", isPaymentFound);
		assertTrue("Payment line not found in a queue", isPaymentLineFound);
		assertTrue("Invoice not found in a queue", isInvoiceFound);
		assertTrue("Invoice line not found in a  queue", isInvoiceLineFound);
		assertTrue("Enrolment not found in a queue", isEnrolmentDound);
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertTrue("11 elements should be replicated for this payment", transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size() == 11);
		//logout
		getReplicationPortType().logout(getCommunicationKey());
	}
	
	void fillV4PaymentStubs(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.toBigDecimal());
		paymentInStub.setContactId(1l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = new ish.oncourse.webservices.v4.stubs.replication.InvoiceStub();
		invoiceStub.setContactId(1l);
		invoiceStub.setAmountOwing(hundredDollars.toBigDecimal());
		invoiceStub.setAngelId(1l);
		invoiceStub.setCreated(current);
		invoiceStub.setDateDue(current);
		invoiceStub.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub.setInvoiceDate(current);
		invoiceStub.setInvoiceNumber(123l);
		invoiceStub.setModified(current);
		invoiceStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		stubs.add(invoiceStub);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentLineStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(paymentInStub.getAmount());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invoiceLineStub = new ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(invoiceStub.getAmountOwing());
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolmentStub = new ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(invoiceLineStub.getAngelId());
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(1l);
		//link the invoiceLine with enrolment
		invoiceLineStub.setEnrolmentId(enrolmentStub.getAngelId());
		stubs.add(enrolmentStub);
		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
	
	@Override
	protected Long getCommunicationKey() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<College> colleges = context.performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		assertFalse("colleges should not be empty", colleges.isEmpty());
		assertTrue("Only 1 college should have id=1", colleges.size() == 1);
		College college = colleges.get(0);
		assertNotNull("Communication key should be not NULL", college.getCommunicationKey());
		return college.getCommunicationKey();
	}
	
	@Override
	protected String getSecurityCode() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<College> colleges = context.performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		assertFalse("colleges should not be empty", colleges.isEmpty());
		assertTrue("Only 1 college should have id=1", colleges.size() == 1);
		College college = colleges.get(0);
		assertNotNull("Security code should be not NULL", college.getWebServicesSecurityCode());
		return college.getWebServicesSecurityCode();
	}

	private ReplicationPortType getReplicationPortType() throws JAXBException {
		return getReplicationPortType(V4_REPLICATION_WSDL, V4_REPLICATION_ENDPOINT_PATH);
	}
	
	private PaymentPortType getPaymentPortType() throws JAXBException {
		return getPaymentPortType(V4_REPLICATION_WSDL, V4_PAYMENT_ENDPOINT_PATH);
	}

}
