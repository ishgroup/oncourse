package ish.oncourse.webservices.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.ServiceModule;
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
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionContext;

public class PaymentInExpireJobTest extends ServiceTest {

	private PaymentInExpireJob job;
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = PaymentInExpireJobTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/jobs/paymentInExpireDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		this.cayenneService = getService(ICayenneService.class);
		this.job = new PaymentInExpireJob(cayenneService);
	}

	@Test
	public void testEnrolmentSpecificFailButNotAbandonExpireCase() throws Exception {

		ObjectContext objectContext = cayenneService.newContext();

		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		p.failPayment();

		objectContext.commitChanges();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);
		
		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource("jdbc/oncourse").getConnection();
			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set modified=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
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
		
		JobExecutionContext jobContext = mock(JobExecutionContext.class);
		job.execute(jobContext);

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
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
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
		JobExecutionContext jobContext = mock(JobExecutionContext.class);
		job.execute(jobContext);

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
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
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
		ObjectContext objectContext = cayenneService.newContext();

		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		p.failPayment();

		objectContext.commitChanges();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);
		
		// simulate EXPIRE_INTERVAL wait by directly updating enrolments with sql statement
		Connection connection = null;
		try {
			connection = getDataSource("jdbc/oncourse").getConnection();
			PreparedStatement prepStat = connection.prepareStatement("update PaymentIn set modified=?");
			prepStat.setDate(1, new java.sql.Date(cal.getTime().getTime()));
			int affected = prepStat.executeUpdate();
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
		
		JobExecutionContext jobContext = mock(JobExecutionContext.class);
		
		//Execute job three times
		job.execute(jobContext);
		job.execute(jobContext);
		job.execute(jobContext);
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn"));
		
		assertEquals("There should be only two PaymentIn", 2, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn where status=4"));
		
		assertEquals("Expecting only one failed paymentIn", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("PaymentIn",
				String.format("select * from PaymentIn where amount=0 and type=5 and status=3"));
		
		assertEquals("There should only be one PaymentIn refund.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityIdentifier='PaymentIn'"));
		
		assertEquals("Expecting two records in the queue for PaymentIn", 2, actualData.getRowCount());
		
		// check that in transaction Payment has failed.
		p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());

		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment has failed.", EnrolmentStatus.FAILED, enrolment.getStatus());

		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 has failed.", EnrolmentStatus.FAILED, enrolment2.getStatus());
	}
}
