package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

public class ReplicationPortTypeTest extends ServiceTest {

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/webservices/soap/v4/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/replicationDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testGetRecordsSuccess() throws Exception {
		
		IReplicationService service = getService(IReplicationService.class);
		
		ReplicationRecords response = (ReplicationRecords) service.getRecords(SupportedVersions.V4);

		assertNotNull("Expecting not null response.", response);

		List<TransactionGroup> groups = response.getGroups();

		assertTrue("Expecting not empty groups.", groups.size() > 0);

		List<ReplicationStub> stubs = groups.get(0).getAttendanceOrBinaryDataOrBinaryInfo();

		assertTrue("Expecting only three stubs.", stubs.size() == 3);

		boolean hasEnrolment = false, hasCourseClass = false, hasDeleteStub = false;

		for (ReplicationStub st : stubs) {
			if (st instanceof EnrolmentStub) {
				hasEnrolment = true;
			}
			if (st instanceof CourseClassStub) {
				hasCourseClass = true;
			}
			if (st instanceof DeletedStub) {
				hasDeleteStub = true;
			}
		}

		assertTrue("Expecting enrolment stub.", hasEnrolment);
		assertTrue("Expecting courseClass stub.", hasCourseClass);
		assertTrue("Expecting DeletedStub", hasDeleteStub);
	}
	
	@Test
	public void testSendRecordsFailToDelete() throws Exception {

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);		
		IReplicationService service = getService(IReplicationService.class);
		
		ITable actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1");
		assertEquals("Initially have one contact with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=200");
		assertEquals("Initially have one student with id.", 1, actualData.getRowCount());
		
		DeletedStub contactDeleteStub = new DeletedStub();
		contactDeleteStub.setWillowId(1l);
		contactDeleteStub.setAngelId(250l);
		contactDeleteStub.setEntityIdentifier("Contact");
		
		DeletedStub studentDeleteStub = new DeletedStub();
		studentDeleteStub.setWillowId(200l);
		studentDeleteStub.setAngelId(1200l);
		studentDeleteStub.setEntityIdentifier("Student");
		
		TransactionGroup group = new TransactionGroup();
		group.getAttendanceOrBinaryDataOrBinaryInfo().add(contactDeleteStub);
		group.getAttendanceOrBinaryDataOrBinaryInfo().add(studentDeleteStub);
		
		ReplicationRecords records = new ReplicationRecords();
		records.getGroups().add(group);
		
		ReplicationResult replResult = (ReplicationResult) service.sendRecords(records);
		
		assertNotNull("Check if replicatin results is not null.", replResult);
		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());
		assertEquals("Expecting to get two replication records.", 2, replResult.getReplicatedRecord().size());
		
		for (ReplicatedRecord r : replResult.getReplicatedRecord()) {
			assertEquals("Expecting FAILED status.", Status.FAILED, r.getStatus());
			String message = r.getMessage();
			assertNotNull("Error message should be set.", message);
		}
		
	}

	@Test
	public void testSendRecordsCreateAndDeleteSucess() throws Exception {

		IReplicationService service = getService(IReplicationService.class);
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		
		ITable actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1658");
		assertEquals("Initially have one contact with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=1540");
		assertEquals("Initially have one student with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("CourseClass", "select * from CourseClass where angelId=123");
		assertEquals("Initially don't have courseClass with angelId.", 0, actualData.getRowCount());
		
		ReplicationRecords records = new ReplicationRecords();

		CourseClassStub rootStub = new CourseClassStub();

		rootStub.setAngelId(123l);
		rootStub.setEntityIdentifier("CourseClass");
		rootStub.setCancelled(false);
		rootStub.setCode("ABC345");
		rootStub.setDetail("CourseDetails");
		rootStub.setFeeExGst(new BigDecimal(1200));
		rootStub.setFeeGst(new BigDecimal(1300));
		rootStub.setMaximumPlaces(23);
		rootStub.setStartDate(new Date());
		rootStub.setCreated(new Date());
		rootStub.setCountOfSessions(3);
		rootStub.setCourseId(1l);
		rootStub.setRoomId(1l);
		
		DeletedStub contactDeleteStub = new DeletedStub();
		contactDeleteStub.setWillowId(1658l);
		contactDeleteStub.setAngelId(2658l);
		contactDeleteStub.setEntityIdentifier("Contact");
		
		DeletedStub studentDeleteStub = new DeletedStub();
		studentDeleteStub.setWillowId(1540l);
		studentDeleteStub.setAngelId(2540l);
		studentDeleteStub.setEntityIdentifier("Student");

		TransactionGroup group = new TransactionGroup();
		group.getAttendanceOrBinaryDataOrBinaryInfo().add(rootStub);
		group.getAttendanceOrBinaryDataOrBinaryInfo().add(contactDeleteStub);
		group.getAttendanceOrBinaryDataOrBinaryInfo().add(studentDeleteStub);

		records.getGroups().add(group);

		GenericReplicationResult replResult = service.sendRecords(records);

		assertNotNull("Check if replicatin results is not null.", replResult);
		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());
		assertEquals("Expecting to get three replication records.", 3, replResult.getReplicatedRecord().size());

		for (GenericReplicatedRecord r : replResult.getReplicatedRecord()) {
			assertEquals("Expecting SUCCESS status.", true, r.isSuccessStatus());
			if ("CourseClass".equalsIgnoreCase(r.getStub().getEntityIdentifier())) {
				assertNotNull("Expecting not null willowId", r.getStub().getWillowId());
			}
		}
		
		// check if courseClass was created
		actualData = dbUnitConnection.createQueryTable("CourseClass", String.format("select * from CourseClass where angelId=123"));
		int numberOfRecords = actualData.getRowCount();
		assertTrue("Expecting one courseClass record.", numberOfRecords == 1);
		
		actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1658");
		assertEquals("Contact should be deleted.", 0, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=1540");
		assertEquals("Student should be deleted.", 0, actualData.getRowCount());
	}

	@Test
	public void testSendResult() throws Exception {

		IReplicationService service = getService(IReplicationService.class);
		
		ReplicationResult result = new ReplicationResult();
		
		ReplicationRecords replicatedRecords = (ReplicationRecords) service.getRecords(SupportedVersions.V4);

		for (TransactionGroup group : replicatedRecords.getGroups()) {
			long angelId = 1;
			for (ReplicationStub stub : group.getAttendanceOrBinaryDataOrBinaryInfo()) {
				
				ReplicatedRecord confirmedRecord = new ReplicatedRecord();
				confirmedRecord.setMessage("Record replicated.");
				confirmedRecord.setStatus(Status.SUCCESS);

				HollowStub confirmedStub = new HollowStub();
				confirmedStub.setEntityIdentifier(stub.getEntityIdentifier());
				confirmedStub.setWillowId(stub.getWillowId());
				confirmedStub.setAngelId(angelId++);

				confirmedRecord.setStub(confirmedStub);
				result.getReplicatedRecord().add(confirmedRecord);
			}
		}

		service.sendResults(result);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));
		
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext objectContext = cayenneService.newContext();
		CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1482);
		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 1);
		
		assertNotNull("Expecting angelId not null for courseClass", courseClass.getAngelId());
		assertNotNull("Expecting angelId not null for enrolment", enrolment.getAngelId());
		
		assertEquals("Check that no queueable records exist for confirmed objects", actualData.getRowCount(), 0);
	}
}
