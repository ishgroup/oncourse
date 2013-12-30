package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.soap.v6.PaymentPortType;
import ish.oncourse.webservices.soap.v6.ReplicationPortType;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import ish.oncourse.webservices.v6.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;

public abstract class RealWSTransportTest extends AbstractTransportTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEProcessDataset.xml";
	protected static final String PAYMENT_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v6/payment";
	protected static final String REPLICATION_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v6/replication";
	protected static final String REPLICATION_WSDL = "wsdl/v6_replication.wsdl";
	protected static final String CARD_HOLDER_NAME = "john smith";
	protected static final String VALID_CARD_NUMBER = "5431111111111111";
	protected static final String DECLINED_CARD_NUMBER = "9999990000000378";
	protected static final String CREDIT_CARD_CVV = "1111";
	//current month and year numbers  as string (convert to string required to pass this data as tapestry params)
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
	protected static final String MEMBERSHIP_IDENTIFIER = Membership.class.getSimpleName();
	protected static final String VOUCHER_IDENTIFIER = Voucher.class.getSimpleName();
	protected static final String ARTICLE_IDENTIFIER = Article.class.getSimpleName();
	protected static final String VOUCHER_PAYMENT_IN_IDENTIFIER = VoucherPaymentIn.class.getSimpleName();

	protected static final int QE_EXPIRE_BY_WATCHDOG_TEST_PORT = 9092;
	protected static final int QE_FAILED_PAYMENT_KEEP_INVOICE_TEST_PORT = 9093;
	protected static final int QE_SUCCESS_PAYMENT_TEST_PORT = 9094;
	protected static final int QE_FAILED_PAYMENT_REVERSE_INVOICE_TEST_PORT = 9095;
	protected static final int QE_PREVIOUSLY_KEEP_ENROLMENT_NEW_INVOICE_TEST_PORT = 9096;
	protected static final int QE_PREVIOUSLY_KEEP_NON_ENROLMENT_NEW_INVOICE_TEST_PORT = 9097;
	protected static final int QE_SUCCESS_PAYMENT_FOR_PARTIALLY_REFUNDED_TEST_PORT = 9098;
	protected static final int QE_SUCCESS_PAYMENT_FOR_PARTIALLY_CANCELED_TEST_PORT = 9099;
	protected static final int QE_FAILED_PAYMENT_FOR_PARTIALLY_REFUNDED_TEST_PORT = 9100;
	protected static final int QE_FAILED_PAYMENT_FOR_PARTIALLY_CANCELED_TEST_PORT = 9101;

	protected static final int QE_COURSE_VOUCHER_REDEEM_SUCCESS_NO_GUI_TEST_PORT = 9102;
	protected static final int QE_COURSE_VOUCHER_REDEEM_FAILED_NO_GUI_TEST_PORT = 9103;
	protected static final int QE_MONEY_VOUCHER_REDEEM_SUCCESS_NO_GUI_TEST_PORT = 9104;
	protected static final int QE_MONEY_VOUCHER_REDEEM_FAILED_NO_GUI_TEST_PORT = 9105;
	protected static final int QE_COURSE_VOUCHER_REDEEM_WITH_MONEY_PAYMENT_SUCCESS_GUI_TEST_PORT = 9106;
	protected static final int QE_MONEY_VOUCHER_REDEEM_WITH_MONEY_PAYMENT_SUCCESS_GUI_TEST_PORT = 9107;
	protected static final int QE_MONEY_VOUCHER_REDEEM_WITH_MONEY_PAYMENT_REVERSE_INVOICE_GUI_TEST_PORT = 9108;
	protected static final int QE_COURSE_VOUCHER_REDEEM_WITH_MONEY_PAYMENT_REVERSE_INVOICE_GUI_TEST_PORT = 9109;

	// TODO: this is ridiculous... why we should define separate server port for each unit test???
	protected static final int QE_VOUCHER_VALIDATION_REQUEST_TEST_PORT = 9110;

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

	protected SupportedVersions getSupportedVersion() {
		return SupportedVersions.V6;
	}

	protected TransactionGroup castGenericTransactionGroup(GenericTransactionGroup transaction) {
		return (TransactionGroup) transaction;
	}
	
	@Before
	public void setup() throws Exception {
		serviceTest = new ServiceTest();
		serviceTest.initTest("ish.oncourse.webservices", "services", PaymentServiceTestModule.class);
		tester = serviceTest.getPageTester();
		InputStream st = RealWSTransportTest.class.getClassLoader().getResourceAsStream(getDataSetFile());
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
		replacementDataSet.addReplacementObject("[null]", null);
        DataSource onDataSource = ServiceTest.getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, replacementDataSet);
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

	protected boolean pingServer() {
		String address = getServer().getServerUrl() + REPLICATION_ENDPOINT_PATH + "?wsdl";
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
		return getReplicationPortType(REPLICATION_WSDL, REPLICATION_ENDPOINT_PATH);
	}
	
	protected PaymentPortType getPaymentPortType() throws JAXBException {
		return getPaymentPortType(REPLICATION_WSDL, PAYMENT_ENDPOINT_PATH);
	}
}
