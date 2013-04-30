package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

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
import org.junit.Before;

public abstract class RealWSTransportTest extends AbstractTransportTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEProcessDataset.xml";
	protected static final String V4_PAYMENT_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v4/payment";
	protected static final String V4_REPLICATION_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v4/replication";
	protected static final String V4_REPLICATION_WSDL = "wsdl/v4_replication.wsdl";
	protected static final String CARD_HOLDER_NAME = "john smith";
	protected static final String VALID_CARD_NUMBER = "5431111111111111";
	protected static final String DECLINED_CARD_NUMBER = "9999990000000378";
	protected static final String CREDIT_CARD_CVV = "1111";
	protected static final String VALID_EXPIRITY_MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1 + StringUtils.EMPTY;
	protected static final String VALID_EXPIRITY_YEAR = Calendar.getInstance().get(Calendar.YEAR) + StringUtils.EMPTY;
	
	protected static final String ID_ATTRIBUTE = "id";
	protected static final String ENROLMENT_IDENTIFIER = Enrolment.class.getSimpleName();
	protected static final String INVOICE_LINE_IDENTIFIER = InvoiceLine.class.getSimpleName();
	protected static final String PAYMENT_LINE_IDENTIFIER = PaymentInLine.class.getSimpleName();
	protected static final String INVOICE_IDENTIFIER = Invoice.class.getSimpleName();
	protected static final String PAYMENT_IDENTIFIER = PaymentIn.class.getSimpleName();
	protected static final String CONTACT_IDENTIFIER = Contact.class.getSimpleName();
	protected static final String STUDENT_IDENTIFIER = Student.class.getSimpleName();
	
	protected ServiceTest serviceTest;
	protected PageTester tester;
	protected ICayenneService cayenneService;
		
	protected static TestServer startRealWSServer(int port) throws Exception {
		TestServer server = new TestServer(port, TestServer.DEFAULT_CONTEXT_PATH, "src/main/webapp/WEB-INF", TestServer.DEFAULT_HOST, 
			"src/main", TestServer.DEFAULT_WEB_XML_FILE_PATH);
		server.start();
		return server;
	} 

	protected void after() throws Exception {
		stopServer(getServer());
	}
	
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}
	
	@Before
	public void setup() throws Exception {
		serviceTest = new ServiceTest();
		serviceTest.initTest("ish.oncourse.webservices", "services", PaymentServiceTestModule.class);
		tester = serviceTest.getPageTester();
		InputStream st = RealWSTransportTest.class.getClassLoader().getResourceAsStream(getDataSetFile());
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
		serviceTest.cleanup();
		after();
	}
		
	protected void fillV4PaymentStubsForCases1_4(GenericTransactionGroup transaction) {
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
	
	protected void fillV4PaymentStubsForCases7(GenericTransactionGroup transaction) {
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
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentLineStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(hundredDollars.multiply(2l).toBigDecimal());//to match original (partially reversed) invoice amount
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(10l);//link with original invoice
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentLineStub2 = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		paymentLineStub2.setAngelId(2l);
		paymentLineStub2.setAmount(Money.ZERO.subtract(hundredDollars).toBigDecimal());//to match reverse invoice amount
		paymentLineStub2.setCreated(current);
		paymentLineStub2.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub2.setInvoiceId(11l);//link with reverse invoice
		paymentLineStub2.setModified(current);
		paymentLineStub2.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub2);
		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
	
	protected void fillV4PaymentStubsForCases8(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money twoHundredDollars = new Money("200.00");
		final Date current = new Date();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(twoHundredDollars.toBigDecimal());
		paymentInStub.setContactId(1l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentLineStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(paymentInStub.getAmount());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(10l);//link with original invoice
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);
		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
	
	protected void fillV4PaymentStubsForCase5_6(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.multiply(2).toBigDecimal());
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
		paymentLineStub.setAmount(hundredDollars.toBigDecimal());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentLine2Stub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		paymentLine2Stub.setAngelId(2l);
		paymentLine2Stub.setAmount(hundredDollars.toBigDecimal());
		paymentLine2Stub.setCreated(current);
		paymentLine2Stub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLine2Stub.setInvoiceId(10l);
		paymentLine2Stub.setModified(current);
		paymentLine2Stub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLine2Stub);
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
		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
	
	protected boolean pingServer() {
		String address = getServer().getServerUrl() + V4_REPLICATION_ENDPOINT_PATH + "?wsdl";
		try {
			URL url = new URL(address);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(1000 * 10); // mTimeout is in seconds
			final long startTime = System.currentTimeMillis();
			urlConn.connect();
			final long endTime = System.currentTimeMillis();
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("Ping time (ms) : " + (endTime - startTime));
				System.out.println("Ping to " + address + " was success");
				return true;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected void authenticate() throws Exception {
		//firstly ping the server with 10 seconds timeout
		assertTrue("Webservices not ready for call", pingServer());
		//authenticate
		Long oldCommunicationKey = getCommunicationKey();
		Long newCommunicationKey = getReplicationPortType().authenticate(getSecurityCode(), oldCommunicationKey);
		assertNotNull("Received communication key should not be empty", newCommunicationKey);
		assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
		assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(getCommunicationKey()) == 0);
	}
		
	protected void logout() throws Exception {
		//logout
		getReplicationPortType().logout(getCommunicationKey());
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

	protected ReplicationPortType getReplicationPortType() throws JAXBException {
		return getReplicationPortType(V4_REPLICATION_WSDL, V4_REPLICATION_ENDPOINT_PATH);
	}
	
	protected PaymentPortType getPaymentPortType() throws JAXBException {
		return getPaymentPortType(V4_REPLICATION_WSDL, V4_PAYMENT_ENDPOINT_PATH);
	}
	
	protected final void renderPaymentPageWithKeepInvoiceProcessing(String sessionId) {
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
		
		Map<String, String> fieldValues = new HashMap<>();
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
	
	protected final void renderPaymentPageWithSuccessProcessing(String sessionId) {
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
		
		Map<String, String> fieldValues = new HashMap<>();
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

}
