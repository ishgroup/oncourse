package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.test.context.ContextUtils;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReplicationPortTypeTest {

	private static final String appPackage = "ish.oncourse.webservices";
	private static final String appName = "App";

	private PageTester tester;

	private <T> T getService(Class<T> serviceInterface) {
		return tester.getService(serviceInterface);
	}

	private static DataSource getDataSource(String location) throws Exception {
		Context context = new InitialContext();
		DataSource dataSource;
		try {
			Context envContext = (Context) context.lookup("java:comp/env");
			dataSource = (DataSource) envContext.lookup(location);
		} catch (NamingException namingEx) {
			dataSource = (DataSource) context.lookup(location);
		}

		return dataSource;
	}

	@Before
	public void setupDataSet() throws Exception {
		ContextUtils.setupDataSources();

		tester = new PageTester(appPackage, appName, "src/main/webapp", ReplicationTestModule.class);

		InputStream st = ReferencePortTypeSoapTest.class.getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = ReferencePortTypeSoapTest.class.getClassLoader().getResourceAsStream("baseReplicationDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@After
	public void cleanDataSet() throws Exception {
		InputStream st = ReferencePortTypeSoapTest.class.getClassLoader().getResourceAsStream("baseReplicationDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.DELETE_ALL.execute(new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null), dataSet);

		st = ReferencePortTypeSoapTest.class.getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DatabaseOperation.DELETE_ALL.execute(new DatabaseConnection(getDataSource("jdbc/oncourse_reference").getConnection(), null),
				dataSet);

		ContextUtils.cleanUpContext();
	}

	@Test
	public void testGetRecordsSuccess() throws Exception {

		ReplicationPortType service = getService(ReplicationPortType.class);

		ReplicationRecords response = service.getRecords();

		assertNotNull("Expecting not null response.", response);

		List<ReplicationStub> stubs = response.getAttendanceOrBinaryDataOrBinaryInfo();

		assertTrue("Expecting only one stub.", stubs.size() == 1);

		assertTrue("Expecting enrolment stub.", stubs.get(0) instanceof EnrolmentStub);

		EnrolmentStub enrlStub = (EnrolmentStub) stubs.get(0);

		assertTrue("Expecting full CourseClass stub inside enrolment.", enrlStub.getCourseClass() instanceof CourseClassStub);

		assertTrue("Expecting hollow stub as a student inside enrolment.", enrlStub.getStudent() instanceof HollowStub);
	}

	@Test
	public void testSendRecords() throws Exception {

		ReplicationPortType service = getService(ReplicationPortType.class);

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

		HollowStub courseStub = new HollowStub();
		courseStub.setAngelId(1l);
		courseStub.setEntityIdentifier("Course");
		courseStub.setWillowId(1l);

		rootStub.setCourse(courseStub);

		HollowStub roomStub = new HollowStub();
		roomStub.setEntityIdentifier("Room");
		roomStub.setAngelId(1l);
		roomStub.setWillowId(1l);

		rootStub.setRoom(roomStub);

		records.getAttendanceOrBinaryDataOrBinaryInfo().add(rootStub);

		ReplicationResult replResult = service.sendRecords(records);

		assertNotNull("Check if replicatin results is not null.", replResult);

		assertNotNull("Check if repl records is not null.", replResult.getReplicatedRecord());

		assertTrue("Expecting to get one replication record.", replResult.getReplicatedRecord().size() == 1);

		// check if courseClass was created
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("CourseClass", String.format("select * from CourseClass where angelId=123"));

		int numberOfRecords = actualData.getRowCount();

		assertTrue("Expecting one courseClass record.", numberOfRecords == 1);

	}

	@Test
	public void testSendResult() throws Exception {

		ReplicationPortType service = getService(ReplicationPortType.class);

		ReplicationResult result = new ReplicationResult();

		ReplicatedRecord confirmedEnrol = new ReplicatedRecord();
		confirmedEnrol.setMessage("Record replicated.");
		confirmedEnrol.setStatus(Status.SUCCESS);

		HollowStub enrlStub = new HollowStub();
		enrlStub.setEntityIdentifier("Enrolment");
		enrlStub.setWillowId(1l);
		enrlStub.setAngelId(125l);

		confirmedEnrol.setStub(enrlStub);

		ReplicatedRecord confirmedCourseClass = new ReplicatedRecord();
		confirmedCourseClass.setMessage("Record replicated.");
		confirmedCourseClass.setStatus(Status.SUCCESS);

		HollowStub courseClassStub = new HollowStub();
		courseClassStub.setEntityIdentifier("CourseClass");
		courseClassStub.setWillowId(1482l);
		courseClassStub.setAngelId(125l);

		confirmedCourseClass.setStub(courseClassStub);

		result.getReplicatedRecord().add(confirmedEnrol);
		result.getReplicatedRecord().add(confirmedCourseClass);

		short resp = service.sendResults(result);

		assertTrue("Expecting zero response.", resp == 0);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));

		assertEquals("Check that no queueable records exist for confirmed objects", actualData.getRowCount(), 0);
	}

	@Test
	public void testSendResultsStatusFail() throws Exception {

		ReplicationPortType port = getService(ReplicationPortType.class);

		ReplicationResult result = new ReplicationResult();

		ReplicatedRecord confirmedEnrol = new ReplicatedRecord();
		confirmedEnrol.setMessage("Record replicated.");
		confirmedEnrol.setStatus(Status.FAILURE);

		HollowStub enrlStub = new HollowStub();
		enrlStub.setEntityIdentifier("Enrolment");
		enrlStub.setWillowId(1l);
		enrlStub.setAngelId(null);

		confirmedEnrol.setStub(enrlStub);

		ReplicatedRecord confirmedCourseClass = new ReplicatedRecord();
		confirmedCourseClass.setMessage("Record replicated.");
		confirmedCourseClass.setStatus(Status.FAILURE);

		HollowStub courseClassStub = new HollowStub();
		courseClassStub.setEntityIdentifier("CourseClass");
		courseClassStub.setWillowId(1482l);
		courseClassStub.setAngelId(null);

		confirmedCourseClass.setStub(courseClassStub);

		result.getReplicatedRecord().add(confirmedEnrol);
		result.getReplicatedRecord().add(confirmedCourseClass);

		short resp = port.sendResults(result);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));

		assertEquals("Expecting two queuable records.", actualData.getRowCount(), 2);

		for (int i = 0; i < actualData.getRowCount(); i++) {
			Integer numberOfAttempts = (Integer) actualData.getValue(i, "numberOfAttempts");
			assertTrue("Expecting numberOfAttempts=1.", numberOfAttempts.equals(1));
		}
	}
}
