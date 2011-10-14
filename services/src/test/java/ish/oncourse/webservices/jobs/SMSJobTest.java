package ish.oncourse.webservices.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.common.types.MessageStatus;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.Pair;
import ish.oncourse.services.message.IMessagePersonService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.sms.ISMSService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SMSJobTest extends ServiceTest {

	private IMessagePersonService messagePersonService;
	private PreferenceControllerFactory prefFactory;
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		messagePersonService = getService(IMessagePersonService.class);
		prefFactory = getService(PreferenceControllerFactory.class);
		cayenneService = getService(ICayenneService.class);

		InputStream st = SMSJobTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/jobs/smsDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testSmsSendingOldMessages() throws Exception {
		ISMSService smsService = mock(ISMSService.class);
		JobExecutionContext jobContext = mock(JobExecutionContext.class);

		when(smsService.authenticate()).thenReturn("123456");
		when(smsService.sendSMS(anyString(), anyString(), anyString(), anyString())).thenReturn(
				new Pair<MessageStatus, String>(MessageStatus.SENT, "success"));

		SMSJob smsJob = new SMSJob(messagePersonService, smsService, prefFactory, cayenneService);
		smsJob.execute(jobContext);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);

		ITable actualData = dbUnitConnection.createQueryTable("MessagePerson", String.format("select * from MessagePerson where status=3"));
		assertEquals("Checking number of FAILED OLD messages.", 6, actualData.getRowCount());

		for (int i = 0; i < actualData.getRowCount(); i++) {
			String response = (String) actualData.getValue(i, "response");
			boolean testResponse = "onCourse-web fails sms messages older than 24 hours. Won't deliver.".equalsIgnoreCase(response)
					|| "remote exception".equalsIgnoreCase(response);
			assertTrue("Checking response.", testResponse);
		}
	}

	@Test
	public void testSmsSendingAuthenticationFatal() throws Exception {
		ISMSService smsService = mock(ISMSService.class);
		JobExecutionContext jobContext = mock(JobExecutionContext.class);

		when(smsService.authenticate()).thenThrow(new RuntimeException("Fatal error."));

		updateCreatedDate();

		SMSJob smsJob = new SMSJob(messagePersonService, smsService, prefFactory, cayenneService);
		
		try {
			smsJob.execute(jobContext);
			fail("Expecting exception here due to failed authentication.");
		}
		catch (JobExecutionException e) {
			
		}
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("MessagePerson", String.format("select * from MessagePerson where status=1"));
		assertEquals("Checking number of QUEUED messages.", 4, actualData.getRowCount());

		ISMSService mock2 = mock(ISMSService.class);

		when(mock2.authenticate()).thenReturn(null);
		when(mock2.sendSMS(anyString(), anyString(), anyString(), anyString())).thenReturn(
				new Pair<MessageStatus, String>(MessageStatus.SENT, "success"));

		smsJob = new SMSJob(messagePersonService, mock2, prefFactory, cayenneService);
		
		try {
			smsJob.execute(jobContext);
			fail("Expecting exception here due to failed authentication.");
		}
		catch (JobExecutionException e) {
			
		}

		actualData = dbUnitConnection.createQueryTable("MessagePerson", String.format("select * from MessagePerson where status=1"));
		assertEquals("Checking number of QUEUED messages.", 4, actualData.getRowCount());

		ISMSService mock3 = mock(ISMSService.class);

		when(mock3.authenticate()).thenReturn("123455");
		when(mock3.sendSMS(anyString(), anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Fatal error"));

		smsJob = new SMSJob(messagePersonService, mock3, prefFactory, cayenneService);
		smsJob.execute(jobContext);

		actualData = dbUnitConnection.createQueryTable("MessagePerson", String.format("select * from MessagePerson where status=3"));
		assertEquals("Checking number of FAILED messages.", 6, actualData.getRowCount());
	}

	@Test
	public void testSmsSendingSuccess() throws Exception {
		ISMSService smsService = mock(ISMSService.class);
		JobExecutionContext jobContext = mock(JobExecutionContext.class);

		when(smsService.authenticate()).thenReturn("123456");
		when(smsService.sendSMS(anyString(), anyString(), anyString(), anyString())).thenReturn(
				new Pair<MessageStatus, String>(MessageStatus.SENT, "success"));

		updateCreatedDate();

		SMSJob smsJob = new SMSJob(messagePersonService, smsService, prefFactory, cayenneService);
		smsJob.execute(jobContext);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("MessagePerson", String.format("select * from MessagePerson where status=2"));
		assertEquals("Checking number of SENT messages.", 4, actualData.getRowCount());

		for (int i = 0; i < actualData.getRowCount(); i++) {
			assertNotNull("Checking if timeOfDelivery was set.", actualData.getValue(i, "timeOfDelivery"));
		}
	}

	private void updateCreatedDate() {

		SelectQuery q = new SelectQuery(MessagePerson.class);

		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<MessagePerson> list = context.performQuery(q);

		Date today = new Date();

		for (MessagePerson m : list) {
			m.setCreated(today);
			m.setModified(today);
		}

		context.commitChanges();
	}
}
