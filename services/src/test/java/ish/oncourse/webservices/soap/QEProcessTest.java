package ish.oncourse.webservices.soap;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;

import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.utils.SessionIdGenerator;
import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.soap.v4.ReplicationService;

import org.apache.cayenne.ObjectContext;
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
	public static final String CARD_HOLDER_NAME = "john smith";
	public static final String VALID_CARD_NUMBER = "5431111111111111";
	public static final String CREDIT_CARD_CVV = "1111";

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
	 */
	@After
	public void cleanup() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		context.deleteObjects(context.performQuery(new SelectQuery(Enrolment.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(CourseClass.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Room.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Site.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Course.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(InvoiceLine.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(PaymentInLine.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Invoice.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(PaymentTransaction.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(PaymentIn.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Student.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Contact.class)));
		
		context.deleteObjects(context.performQuery(new SelectQuery(Preference.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(Country.class)));
		
		context.deleteObjects(context.performQuery(new SelectQuery(QueuedRecord.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(QueuedTransaction.class)));
		context.deleteObjects(context.performQuery(new SelectQuery(College.class)));
		context.commitChanges();
		System.out.println("data cleaned.");
	}
	
	@Test
	public void testRenderPaymentPage() {
		String sessionId = preparePayment();
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
		fieldValues.put(cardName.getAttribute("id"), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute("id"), VALID_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute("id"), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute("id"), "01");
		fieldValues.put(expirityYear.getAttribute("id"), "2019");
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
		Element successPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-success");
		assertNotNull("Success payment div should be loaded", successPaymentDiv);
		Element output = successPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Success payment output should be loaded", output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		Node successMessage = output.getChildren().get(0);
		assertNotNull("Success message should be included", successMessage);
		assertEquals("Unexpected message", "Payment was successful.", successMessage.toString());
	}
	
	private String preparePayment() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<College> colleges = context.performQuery(new SelectQuery(College.class));
		assertNotNull("1", colleges);
		assertTrue("3", colleges.size() == 1);
		@SuppressWarnings("unchecked")
		List<Contact> contacts = context.performQuery(new SelectQuery(Contact.class));
		assertNotNull("1", contacts);
		assertTrue("3", contacts.size() == 1);
		@SuppressWarnings("unchecked")
		List<CourseClass> classes = context.performQuery(new SelectQuery(CourseClass.class));
		assertNotNull("1", classes);
		assertTrue("3", classes.size() == 1);
		Invoice invoice = context.newObject(Invoice.class);
		invoice.setCollege(colleges.get(0));
		invoice.setContact(contacts.get(0));
		invoice.setSource(PaymentSource.SOURCE_ONCOURSE);
		invoice.setAngelId(1l);
		final Money hundredDollars = new Money("100.00");
		invoice.setAmountOwing(hundredDollars.toBigDecimal());
		final Date current = new Date();
		invoice.setCreated(current);
		invoice.setModified(current);
		invoice.setDateDue(current);
		invoice.setInvoiceNumber(123l);
		invoice.setTotalGst(invoice.getAmountOwing());
		invoice.setTotalExGst(invoice.getAmountOwing());
		invoice.setInvoiceDate(current);
		
		PaymentIn payment = context.newObject(PaymentIn.class);
		payment.setAmount(hundredDollars.toBigDecimal());
		payment.setAngelId(1l);
		payment.setCollege(invoice.getCollege());
		payment.setContact(invoice.getContact());
		payment.setCreated(current);
		payment.setModified(current);
		payment.setSessionId(new SessionIdGenerator().generateSessionId());
		payment.setSource(PaymentSource.SOURCE_ONCOURSE);
		payment.setStatus(PaymentStatus.IN_TRANSACTION);
		payment.setType(PaymentType.CREDIT_CARD);
		payment.setStudent(payment.getContact().getStudent());
		
		PaymentInLine paymentLine = context.newObject(PaymentInLine.class);
		paymentLine.setAmount(payment.getAmount());
		paymentLine.setAngelId(1l);
		paymentLine.setCollege(payment.getCollege());
		paymentLine.setCreated(current);
		paymentLine.setInvoice(invoice);
		paymentLine.setModified(current);
		paymentLine.setPaymentIn(payment);
		
		InvoiceLine invoiceLine  = context.newObject(InvoiceLine.class);
		invoiceLine.setAngelId(1l);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setCreated(current);
		invoiceLine.setDescription(StringUtils.EMPTY);
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setInvoice(invoice);
		invoiceLine.setModified(current);
		invoiceLine.setPriceEachExTax(hundredDollars);
		invoiceLine.setQuantity(BigDecimal.ONE);
		invoiceLine.setTaxEach(Money.ZERO);
		invoiceLine.setTitle(StringUtils.EMPTY);
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setAngelId(1l);
		enrolment.setCollege(invoice.getCollege());
		enrolment.setCourseClass(classes.get(0));
		enrolment.setCreated(current);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setModified(current);
		enrolment.setSource(PaymentSource.SOURCE_ONCOURSE);
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(payment.getStudent());
		
		context.commitChanges();
		return payment.getSessionId();
	}
	
	@Test
	public void testReplicationPortType_authenticate() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.authenticate(getSecurityCode(), getCommunicationKey());
	}

	@Override
	protected Long getCommunicationKey() {
		return Long.valueOf("7059522699886202880");
	}
	
	@Override
	protected String getSecurityCode() {
		return "345ttn44$%9";
	}

	private ReplicationPortType getReplicationPortType() throws JAXBException {
		ReplicationService replicationService = new ReplicationService(ReplicationPortType.class.getClassLoader().getResource("wsdl/v4_replication.wsdl"));
		ReplicationPortType replicationPortType = replicationService.getReplicationPort();
		initPortType((BindingProvider) replicationPortType, TestServer.DEFAULT_CONTEXT_PATH + "/v4/replication");
		return replicationPortType;
	}

}
