package ish.oncourse.webservices.jobs;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.PaymentInSupport;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.util.payment.PaymentInFail;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PaymentInExpireJobTest extends ServiceTest {

	private PaymentInExpireJob job;
	private ICayenneService cayenneService;
	private PreferenceControllerFactory prefFactory;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ReplicationTestModule.class);

		new LoadDataSet().dataSetFile("ish/oncourse/webservices/jobs/paymentInExpireDataSet.xml")
				.load(testContext.getDS());

		this.cayenneService = getService(ICayenneService.class);
		this.prefFactory = getService(PreferenceControllerFactory.class);
		this.job = new PaymentInExpireJob(cayenneService, getService(IPaymentService.class), prefFactory);
	}

	@After
	public void after() {
		testContext.close();
	}

	@Test
	public void testEnrolmentSpecificFailButNotAbandonExpireCase() throws Exception {

		ObjectContext objectContext = cayenneService.newContext();

		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(p).build().getModel();
		PaymentInFail.valueOf(model).perform();

		PaymentIn newCopy = p.makeCopy();
		newCopy.setStatus(PaymentStatus.IN_TRANSACTION);
		newCopy.getObjectContext().commitChanges();

		objectContext.commitChanges();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);

		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			PreparedStatement prepStat = connection.prepareStatement(String.format("update PaymentIn set modified=? where id=%s", newCopy.getId()));
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 1 paymentIn.", 1, affected);
			prepStat.close();

			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
			prepStat = connection.prepareStatement(String.format("update PaymentIn set created=? where id=%s", newCopy.getId()));
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			affected = prepStat.executeUpdate();
			assertEquals("Expected update on 1 paymentIn.", 1, affected);

			prepStat.close();

			//cleanup the queue before running job
			Statement st = connection.createStatement();
			st.execute("delete from QueuedRecord");
			st.execute("delete from QueuedTransaction");
			st.close();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		job.execute();

		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		objectContext = cayenneService.newContext();

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment should be in transaction.", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 should be in transaction.", EnrolmentStatus.IN_TRANSACTION, enrolment2.getStatus());

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource().getConnection(), null);

		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=%s", newCopy.getId()));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=%s", newCopy.getPaymentInLines().get(0).getId()));
		assertEquals("1 PaymentInLine in the queue.", 1, actualData.getRowCount());
	}

	@Test
	public void testExecute() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);

		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = testContext.getDS().getConnection();

			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set created=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 4 paymentIn.", 4, affected);

			prepStat.close();

			//cleanup the queue before running job
			Statement st = connection.createStatement();
			st.execute("delete from QueuedRecord");
			st.execute("delete from QueuedTransaction");
			st.close();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		job.execute();

		// check that in transaction Payment has failed.
		ObjectContext objectContext = cayenneService.newContext();
		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		p = Cayenne.objectForPK(objectContext, PaymentIn.class, 1001);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment should be in transaction.", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 should be in transaction.", EnrolmentStatus.IN_TRANSACTION, enrolment2.getStatus());

		// check the queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource().getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
	}

	@Test
	public void testMultyExecution() throws Exception {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);

		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set modified=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 4 paymentIn.", 4, affected);

			prepStat.close();

			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
			prepStat = connection.prepareStatement("update PaymentIn set created=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			affected = prepStat.executeUpdate();
			assertEquals("Expected update on 4 paymentIn.", 4, affected);

			prepStat.close();

			//cleanup the queue before running job
			Statement st = connection.createStatement();
			st.execute("delete from QueuedRecord");
			st.execute("delete from QueuedTransaction");
			st.close();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		//Execute job three times
		job.execute();
		job.execute();
		job.execute();

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource().getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn"));

		assertEquals("There should be only four PaymentIn", 4, actualData.getRowCount());

		actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn where status=4"));

		assertEquals("Expecting only three failed paymentIn", 3, actualData.getRowCount());

		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn'"));

		assertEquals("Expecting 3 records in the queue for PaymentIn", 3, actualData.getRowCount());

		ObjectContext objectContext = cayenneService.newContext();

		// check that in transaction Payment has failed.
		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		p = Cayenne.objectForPK(objectContext, PaymentIn.class, 1001);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		p = Cayenne.objectForPK(objectContext, PaymentIn.class, 20000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 20000);
		assertEquals("Enrolment should be in transaction.", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 20010);
		assertEquals("Enrolment2 is active.", EnrolmentStatus.SUCCESS, enrolment2.getStatus());
	}

	@Test
	public void testUnprocessedPaymentExpiration() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);

		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn payment1 = Cayenne.objectForPK(context, PaymentIn.class, 2000);
		PaymentIn payment2 = Cayenne.objectForPK(context, PaymentIn.class, 20000);

		Enrolment enrolment1 = Cayenne.objectForPK(context, Enrolment.class, 2000);
		Enrolment enrolment2 = Cayenne.objectForPK(context, Enrolment.class, 2001);

		payment1.setCreated(cal.getTime());
		payment2.setCreated(cal.getTime());

		context.commitChanges();

		PaymentInSupport paymentSupport1 = new PaymentInSupport(payment1, cayenneService);
		PaymentTransaction transaction1 = paymentSupport1.createTransaction();
		paymentSupport1.commitTransaction();

		PaymentInSupport paymentSupport2 = new PaymentInSupport(payment2, cayenneService);
		PaymentTransaction transaction2 = paymentSupport2.createTransaction();
		paymentSupport2.commitTransaction();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL - 1);

		Connection connection = null;
		try {
			connection = getDataSource().getConnection();

			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set modified=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 4 paymentIn.", 4, affected);

			prepStat.close();

			//cleanup the queue before running job
			Statement st = connection.createStatement();
			st.execute("delete from QueuedRecord");
			st.execute("delete from QueuedTransaction");
			st.close();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		job.execute();

		payment1.setPersistenceState(PersistenceState.HOLLOW);
		payment2.setPersistenceState(PersistenceState.HOLLOW);
		enrolment1.setPersistenceState(PersistenceState.HOLLOW);
		enrolment2.setPersistenceState(PersistenceState.HOLLOW);

		// Payments have unfinalized transactions and therefore should not be expired
		assertEquals(PaymentStatus.IN_TRANSACTION, payment1.getStatus());
		assertEquals(PaymentStatus.IN_TRANSACTION, payment2.getStatus());

		assertEquals(EnrolmentStatus.IN_TRANSACTION, enrolment1.getStatus());
		assertEquals(EnrolmentStatus.IN_TRANSACTION, enrolment2.getStatus());

		transaction1.setIsFinalised(true);
		transaction2.setIsFinalised(true);

		paymentSupport1.commitTransaction();
		paymentSupport2.commitTransaction();

		job.execute();

		payment1.setPersistenceState(PersistenceState.HOLLOW);
		payment2.setPersistenceState(PersistenceState.HOLLOW);
		enrolment1.setPersistenceState(PersistenceState.HOLLOW);
		enrolment2.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(PaymentStatus.FAILED, payment1.getStatus());
		assertEquals(PaymentStatus.FAILED, payment2.getStatus());

		assertEquals(EnrolmentStatus.IN_TRANSACTION, enrolment1.getStatus());
		assertEquals(EnrolmentStatus.IN_TRANSACTION, enrolment2.getStatus());

	}

	@Test
	public void testSuccessTransaction() throws Exception {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);

		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			PreparedStatement prepStat = connection.prepareStatement(String.format("update PaymentIn set modified=? where id=%s", 20000));
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 1 paymentIn.", 1, affected);
			prepStat.close();

			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
			prepStat = connection.prepareStatement(String.format("update PaymentIn set created=? where id=%s", 20000));
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			affected = prepStat.executeUpdate();
			assertEquals("Expected update on 1 paymentIn.", 1, affected);

			prepStat.close();

			Statement statement = connection.createStatement();
			statement.execute("\n" +
					"\n" +
					"INSERT INTO PaymentTransaction\n" +
					"(paymentId, isFinalised, created, modified, txnReference, response, soapResponse)\n" +
					"VALUES(20000, 1, '2017-02-13 23:44:25','2017-02-13 23:44:25','O2000', 'APPROVED (00)', '<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><SubmitTransactionResponse xmlns=\"http://PaymentExpress.com\"><SubmitTransactionResult><acquirerReco>00</acquirerReco><acquirerResponseText>APPROVED</acquirerResponseText><amount>700.00</amount><authCode>030624</authCode><authorized>1</authorized><billingId/><cardHolderHelpText>The Transaction was approved</cardHolderHelpText><cardHolderName>JOHN SMITH</cardHolderName><cardHolderResponseDescription>The Transaction was approved</cardHolderResponseDescription><cardHolderResponseText>APPROVED</cardHolderResponseText><cardName>MasterCard</cardName><cardNumber>543111........11</cardNumber><currencyId>36</currencyId><currencyName>AUD</currencyName><currencyRate>1.00</currencyRate><cvc2/><dateExpiry>1127</dateExpiry><dateSettlement>20170208</dateSettlement><dpsBillingId>0000030179087440</dpsBillingId><dpsTxnRef>000000032e9ddea3</dpsTxnRef><helpText>Transaction Approved</helpText><merchantHelpText>The Transaction was approved</merchantHelpText><merchantReference>O1365562</merchantReference><merchantResponseDescription>The Transaction was approved</merchantResponseDescription><merchantResponseText>APPROVED</merchantResponseText><reco>00</reco><responseText>APPROVED</responseText><retry>0</retry><statusRequired>0</statusRequired><testMode>0</testMode><txnRef>O1365562</txnRef><txnType>Purchase</txnType><iccData/><cardNumber2/><issuerCountryId/><txnMac>25FA3AFA</txnMac><cvc2ResultCode>NotUsed</cvc2ResultCode><riskRuleMatches/><extendedData/></SubmitTransactionResult></SubmitTransactionResponse></soap:Body></soap:Envelope>')");

		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		job.execute();

		ObjectContext objectContext = cayenneService.newContext();


		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 20000);
		assertEquals("Payment hasn't became success.", PaymentStatus.SUCCESS, p.getStatus());
		Enrolment e = Cayenne.objectForPK(objectContext, Enrolment.class, 20000);
		assertEquals("Payment has changed status but it can't.", EnrolmentStatus.IN_TRANSACTION,e.getStatus());
		Enrolment e1 = Cayenne.objectForPK(objectContext, Enrolment.class, 20010);
		assertEquals("Payment has changed status but it can't.", EnrolmentStatus.IN_TRANSACTION,e.getStatus());

	}


}
