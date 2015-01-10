package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ReplicationPortTypeTest extends ServiceTest {

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/replicationDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testV4GetRecordsSuccess() throws Exception {
		testGetRecordsSuccess(SupportedVersions.V6);
	}
	
	private void testGetRecordsSuccess(SupportedVersions version) throws Exception {
		IReplicationService service = getService(IReplicationService.class);
		GenericReplicationRecords response = service.getRecords(version);
		assertNotNull("Expecting not null response.", response);
		List<GenericTransactionGroup> groups = response.getGenericGroups();
		assertTrue("Expecting not empty groups.", groups.size() > 0);
		List<GenericReplicationStub> stubs = groups.get(0).getGenericAttendanceOrBinaryDataOrBinaryInfo();
		assertTrue("Expecting only three stubs.", stubs.size() == 3);
		boolean hasEnrolment = false, hasCourseClass = false, hasDeleteStub = false;
		for (GenericReplicationStub st : stubs) {
			if (st instanceof GenericEnrolmentStub) {
				hasEnrolment = true;
			}
			if (CourseClass.class.getSimpleName().equals(st.getEntityIdentifier()) && !(st instanceof GenericDeletedStub)) {
				hasCourseClass = true;
			}
			if (st instanceof GenericDeletedStub) {
				hasDeleteStub = true;
			}
		}

		assertTrue("Expecting enrolment stub.", hasEnrolment);
		assertTrue("Expecting courseClass stub.", hasCourseClass);
		assertTrue("Expecting DeletedStub", hasDeleteStub);
	}
	
	@Test
	public void testV6SendRecordsFailToDelete() throws Exception {
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);		
		IReplicationService service = getService(IReplicationService.class);
		ITable actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1");
		assertEquals("Initially have one contact with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=200");
		assertEquals("Initially have one student with id.", 1, actualData.getRowCount());
		
		ish.oncourse.webservices.v6.stubs.replication.DeletedStub contactDeleteStub = new ish.oncourse.webservices.v6.stubs.replication.DeletedStub();
		contactDeleteStub.setWillowId(1l);
		contactDeleteStub.setAngelId(250l);
		contactDeleteStub.setEntityIdentifier("Contact");
		ish.oncourse.webservices.v6.stubs.replication.DeletedStub studentDeleteStub = new ish.oncourse.webservices.v6.stubs.replication.DeletedStub();
		studentDeleteStub.setWillowId(200l);
		studentDeleteStub.setAngelId(1200l);
		studentDeleteStub.setEntityIdentifier("Student");
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V6);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactDeleteStub);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(studentDeleteStub);
		GenericReplicationRecords records = PortHelper.createReplicationRecords(SupportedVersions.V6);
		records.getGenericGroups().add(group);
		GenericReplicationResult replResult = service.sendRecords(records);
		
		assertNotNull("Check if replicatin results is not null.", replResult);
		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());
		assertEquals("Expecting to get two replication records.", 2, replResult.getReplicatedRecord().size());
		
		for (GenericReplicatedRecord r : replResult.getGenericReplicatedRecord()) {
			assertEquals("Expecting FAILED status.", true, StubUtils.hasFailedStatus(r));
			String message = r.getMessage();
			assertNotNull("Error message should be set.", message);
		}
		
	}

	@Test
	public void testV6SendRecordsCreateAndDeleteSuccess() throws Exception {

		IReplicationService service = getService(IReplicationService.class);
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		
		ITable actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1658");
		assertEquals("Initially have one contact with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=1540");
		assertEquals("Initially have one student with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("CourseClass", "select * from CourseClass where angelId=123");
		assertEquals("Initially don't have courseClass with angelId.", 0, actualData.getRowCount());
		
		GenericReplicationRecords records = PortHelper.createReplicationRecords(SupportedVersions.V6);

		ish.oncourse.webservices.v6.stubs.replication.CourseClassStub rootStub = new ish.oncourse.webservices.v6.stubs.replication.CourseClassStub();

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
		rootStub.setDistantLearningCourse(false);
		
		ish.oncourse.webservices.v6.stubs.replication.DeletedStub contactDeleteStub = new ish.oncourse.webservices.v6.stubs.replication.DeletedStub();
		contactDeleteStub.setWillowId(1658l);
		contactDeleteStub.setAngelId(2658l);
		contactDeleteStub.setEntityIdentifier("Contact");
		
		ish.oncourse.webservices.v6.stubs.replication.DeletedStub studentDeleteStub = new ish.oncourse.webservices.v6.stubs.replication.DeletedStub();
		studentDeleteStub.setWillowId(1540l);
		studentDeleteStub.setAngelId(2540l);
		studentDeleteStub.setEntityIdentifier("Student");

		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V6);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(rootStub);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactDeleteStub);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(studentDeleteStub);

		records.getGenericGroups().add(group);

		GenericReplicationResult replResult = service.sendRecords(records);

		assertNotNull("Check if replicatin results is not null.", replResult);
		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());
		assertEquals("Expecting to get three replication records.", 3, replResult.getReplicatedRecord().size());

		for (GenericReplicatedRecord r : replResult.getReplicatedRecord()) {
			assertEquals("Expecting SUCCESS status.", true, StubUtils.hasSuccessStatus(r));
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
	public void testV6SendResult() throws Exception {
		testSendResult(SupportedVersions.V6, false);
	}
	
	@Test
	public void testV6SendResultWithConcurentDelete() throws Exception {
		testSendResult(SupportedVersions.V6, true);
	}
	
	private void testSendResult(SupportedVersions version, boolean withConcurentDelete) throws Exception {
		IReplicationService service = getService(IReplicationService.class);
		GenericReplicationResult result = PortHelper.createReplicationResult(version);
		GenericReplicationRecords replicatedRecords = service.getRecords(version);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext objectContext = cayenneService.newNonReplicatingContext();
		if (withConcurentDelete) {
			//CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1482);
			Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 1);
			assertNotNull("Enrolment should exist", enrolment);
			assertNotNull("Enrolment should be linked with invoice line", enrolment.getOriginalInvoiceLine());
			List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>(enrolment.getInvoiceLines());
			for (InvoiceLine invoiceLine : invoiceLines) {
				invoiceLine.setEnrolment(null);
			}
			objectContext.deleteObjects(enrolment);
			objectContext.commitChanges();
		}
		for (GenericTransactionGroup group : replicatedRecords.getGenericGroups()) {
			long angelId = 1;
			for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
				stub.setAngelId(angelId++);
				GenericReplicatedRecord confirmedRecord = ReplicationUtils.toReplicatedRecord(stub, true);
				confirmedRecord.setMessage("Record replicated.");
				result.getGenericReplicatedRecord().add(confirmedRecord);
			}
		}

		service.sendResults(result);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));

		//use new context to check the data
		objectContext = cayenneService.newNonReplicatingContext();
		CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1482);
		Enrolment enrolment = Cayenne.objectForPK(objectContext, Enrolment.class, 1);
		
		assertNotNull("Expecting angelId not null for courseClass", courseClass.getAngelId());
		if (withConcurentDelete) {
			assertNull("Expecting null for this enrolment", enrolment);
			assertEquals("Check that 1 queueable records exist for confirmed objects", actualData.getRowCount(), 1);
		} else {
			assertNotNull("Expecting angelId not null for enrolment", enrolment.getAngelId());
			assertEquals("Check that no queueable records exist for confirmed objects", actualData.getRowCount(), 0);
		}
		if (withConcurentDelete) {
			replicatedRecords = service.getRecords(version);
			assertTrue("Replication should be empty", replicatedRecords.getGenericGroups().isEmpty());
			actualData = dbUnitConnection.createQueryTable("QueuedRecord", 
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));
			assertEquals("Check that no queueable records exist for confirmed objects", actualData.getRowCount(), 0);
		}
	}
	
	@Test
	public void testV7GetRecordsSuccess() throws Exception {
		testGetRecordsSuccess(SupportedVersions.V7);
	}
	
	@Test
	public void testV7SendRecordsFailToDelete() throws Exception {
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);		
		IReplicationService service = getService(IReplicationService.class);
		ITable actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1");
		assertEquals("Initially have one contact with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=200");
		assertEquals("Initially have one student with id.", 1, actualData.getRowCount());
		
		ish.oncourse.webservices.v7.stubs.replication.DeletedStub contactDeleteStub = new ish.oncourse.webservices.v7.stubs.replication.DeletedStub();
		contactDeleteStub.setWillowId(1l);
		contactDeleteStub.setAngelId(250l);
		contactDeleteStub.setEntityIdentifier("Contact");
		ish.oncourse.webservices.v7.stubs.replication.DeletedStub studentDeleteStub = new ish.oncourse.webservices.v7.stubs.replication.DeletedStub();
		studentDeleteStub.setWillowId(200l);
		studentDeleteStub.setAngelId(1200l);
		studentDeleteStub.setEntityIdentifier("Student");
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V7);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactDeleteStub);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(studentDeleteStub);
		GenericReplicationRecords records = PortHelper.createReplicationRecords(SupportedVersions.V7);
		records.getGenericGroups().add(group);
		GenericReplicationResult replResult = service.sendRecords(records);
		
		assertNotNull("Check if replicatin results is not null.", replResult);
		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());
		assertEquals("Expecting to get two replication records.", 2, replResult.getReplicatedRecord().size());
		
		for (GenericReplicatedRecord r : replResult.getGenericReplicatedRecord()) {
			assertEquals("Expecting FAILED status.", true, StubUtils.hasFailedStatus(r));
			String message = r.getMessage();
			assertNotNull("Error message should be set.", message);
		}
		
	}
	
	@Test
	public void testV7SendRecordsCreateAndDeleteSucess() throws Exception {

		IReplicationService service = getService(IReplicationService.class);
		
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		
		ITable actualData = dbUnitConnection.createQueryTable("Contact", "select * from Contact where id=1658");
		assertEquals("Initially have one contact with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("Student", "select * from Student where id=1540");
		assertEquals("Initially have one student with id.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("CourseClass", "select * from CourseClass where angelId=123");
		assertEquals("Initially don't have courseClass with angelId.", 0, actualData.getRowCount());
		
		GenericReplicationRecords records = PortHelper.createReplicationRecords(SupportedVersions.V7);

		ish.oncourse.webservices.v7.stubs.replication.CourseClassStub rootStub = new ish.oncourse.webservices.v7.stubs.replication.CourseClassStub();

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
		rootStub.setDistantLearningCourse(false);
		
		ish.oncourse.webservices.v7.stubs.replication.DeletedStub contactDeleteStub = new ish.oncourse.webservices.v7.stubs.replication.DeletedStub();
		contactDeleteStub.setWillowId(1658l);
		contactDeleteStub.setAngelId(2658l);
		contactDeleteStub.setEntityIdentifier("Contact");
		
		ish.oncourse.webservices.v7.stubs.replication.DeletedStub studentDeleteStub = new ish.oncourse.webservices.v7.stubs.replication.DeletedStub();
		studentDeleteStub.setWillowId(1540l);
		studentDeleteStub.setAngelId(2540l);
		studentDeleteStub.setEntityIdentifier("Student");

		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V7);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(rootStub);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactDeleteStub);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(studentDeleteStub);

		records.getGenericGroups().add(group);

		GenericReplicationResult replResult = service.sendRecords(records);

		assertNotNull("Check if replicatin results is not null.", replResult);
		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());
		assertEquals("Expecting to get three replication records.", 3, replResult.getReplicatedRecord().size());

		for (GenericReplicatedRecord r : replResult.getReplicatedRecord()) {
			assertEquals("Expecting SUCCESS status.", true, StubUtils.hasSuccessStatus(r));
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
	public void testV7SendResult() throws Exception {
		testSendResult(SupportedVersions.V7, false);
	}
	
	@Test
	public void testV7SendResultWithConcurentDelete() throws Exception {
		testSendResult(SupportedVersions.V7, true);
	}
}
