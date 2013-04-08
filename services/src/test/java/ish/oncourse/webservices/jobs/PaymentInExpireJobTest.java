package ish.oncourse.webservices.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.PaymentInSupport;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

public class PaymentInExpireJobTest extends ServiceTest {

	private PaymentInExpireJob job;
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = PaymentInExpireJobTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/webservices/jobs/paymentInExpireDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		this.cayenneService = getService(ICayenneService.class);
		this.job = new PaymentInExpireJob(cayenneService, getService(IPaymentService.class));
	}

	@Test
	public void testEnrolmentSpecificFailButNotAbandonExpireCase() throws Exception {

		ObjectContext objectContext = cayenneService.newContext();

		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		p.failPayment();
		
		PaymentIn newCopy = p.makeCopy();
		newCopy.setStatus(PaymentStatus.IN_TRANSACTION);
		newCopy.getObjectContext().commitChanges();
		
		objectContext.commitChanges();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);
		
		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource("jdbc/oncourse").getConnection();
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

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment has failed.", EnrolmentStatus.FAILED, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 has failed.", EnrolmentStatus.FAILED, enrolment2.getStatus());

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select transactionId from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=%s", newCopy.getId()));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=%s", newCopy.getPaymentInLines().get(0).getId()));
		assertEquals("1 PaymentInLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertTrue("at least 1 Enrolment in the queue.", 1 <= actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2001"));
		assertTrue("at least 1 Enrolment in the queue.", 1 <= actualData.getRowCount());
	}

	@Test
	public void testExecute() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
		
		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource("jdbc/oncourse").getConnection();
			
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set created=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 2 paymentIn.", 2, affected);
			
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

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment has failed.", EnrolmentStatus.FAILED, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 has failed.", EnrolmentStatus.FAILED, enrolment2.getStatus());

		// check the queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select transactionId from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("2 PaymentIn in the queue.", 2, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertTrue("at least 1 Enrolment in the queue.", 1 <= actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2001"));
		assertTrue("at least 1 Enrolment in the queue.", 1 <= actualData.getRowCount());
	}
	
	@Test
	public void testMultyExecution() throws Exception {
	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);
		
		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource("jdbc/oncourse").getConnection();
			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set modified=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 2 paymentIn.", 2, affected);
			
			prepStat.close();
			
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW + 1);
			prepStat = connection.prepareStatement("update PaymentIn set created=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			affected = prepStat.executeUpdate();
			assertEquals("Expected update on 2 paymentIn.", 2, affected);
			
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
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn"));
		
		assertEquals("There should be only three PaymentIn", 3, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn where status=4"));
		
		assertEquals("Expecting only two failed paymentIn", 2, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn where amount=0 and type=5 and status=3"));
		
		assertEquals("There should only be one PaymentIn refund.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn'"));
		
		assertEquals("Expecting 4 records in the queue for PaymentIn", 4, actualData.getRowCount());
		
		ObjectContext objectContext = cayenneService.newContext();
		
		// check that in transaction Payment has failed.
		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());
		
		p = Cayenne.objectForPK(objectContext, PaymentIn.class, 20000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment has failed.", EnrolmentStatus.FAILED, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 has failed.", EnrolmentStatus.FAILED, enrolment2.getStatus());
		
		enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 20000);
		assertEquals("Enrolment is active.", EnrolmentStatus.SUCCESS, enrolment.getStatus());

		enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 20010);
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
			connection = getDataSource("jdbc/oncourse").getConnection();

			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set modified=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
			assertEquals("Expected update on 2 paymentIn.", 2, affected);
			
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
		
		assertEquals(EnrolmentStatus.FAILED, enrolment1.getStatus());
		assertEquals(EnrolmentStatus.FAILED, enrolment2.getStatus());

	}
	
}
