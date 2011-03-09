package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.Status;

import java.io.InputStream;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

public class ReplicationSendRecordsFailTest extends AbstractReplicationTest {
	
	@Before
	public void setupDataSet() throws Exception {
		InputStream st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE_REFERENCE).getConnection(), null),
				dataSet);

		st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("baseReplicationDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DatabaseOperation.INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null), dataSet);
	}
	
	@Test
	public void testSendResultsStatusFail() throws Exception {
		AuthenticationPortType authPort = getAuthPort();

		String securityCode = "345ttn44$%9";
		long newCommKey = authPort.authenticate(securityCode, 7059522699886202880l);

		ReplicationPortType port = getReplicationPort();

		Client client = ClientProxy.getClient(port);
		client.getOutInterceptors().add(new AddSecurityCodeInterceptor(securityCode, newCommKey));

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

		authPort.logout(newCommKey);

		DatabaseConnection dbUnitConnection = new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));

		assertEquals("Expecting two queuable records.", actualData.getRowCount(), 2);

		for (int i = 0; i < actualData.getRowCount(); i++) {
			Integer numberOfAttempts = (Integer) actualData.getValue(i, "numberOfAttempts");
			assertTrue("Expecting numberOfAttempts=1.", numberOfAttempts.equals(1));
		}
	}
}
