package ish.oncourse.services.jobs;

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

import java.io.InputStream;

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
		
		InputStream st = PaymentInExpireJobTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/jobs/paymentInExpireDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
		this.job = new PaymentInExpireJob(cayenneService);
	}
	
	@Test
	public void testExecute() throws Exception {
		JobExecutionContext jobContext = mock(JobExecutionContext.class);
		job.execute(jobContext);
		
		//check that in transaction Payment has failed.
		ObjectContext objectContext = cayenneService.newContext();
		PaymentIn p = Cayenne.objectForPK(objectContext, PaymentIn.class, 2000);
		assertEquals("Payment has failed.", PaymentStatus.FAILED, p.getStatus());
		
		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 2000);
		assertEquals("Enrolment has failed.", EnrolmentStatus.FAILED, enrolment.getStatus());
		
		Enrolment enrolment2 = Cayenne.objectForPK(objectContext, Enrolment.class, 2001);
		assertEquals("Enrolment2 has failed.", EnrolmentStatus.FAILED, enrolment2.getStatus());
		
		//check the queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select transactionId from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertTrue("at least 1 Enrolment in the queue.", 1 <= actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2001"));
		assertTrue("at least 1 Enrolment in the queue.", 1 <= actualData.getRowCount());
	}
}
