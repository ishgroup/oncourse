package ish.oncourse.webservices.soap.v4;

import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.util.*;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReplicationSendFailedResultTest extends ServiceTest {
	
	@Before
	public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		
		InputStream st = ReplicationSendFailedResultTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/replicationDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);

	}
	
	@Test
	public void testV4SendResultsStatusFail() throws Exception {
		IReplicationService service = getService(IReplicationService.class);
		GenericReplicationResult result = PortHelper.createReplicationResult(SupportedVersions.V4);
		GenericReplicationRecords replicatedRecords = service.getRecords(SupportedVersions.V4);
		for (GenericTransactionGroup group : replicatedRecords.getGenericGroups()) {
			for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
				GenericReplicatedRecord confirmedRecord = ReplicationUtils.toReplicatedRecord(stub, true);
				confirmedRecord.setMessage("Record replicated.");
				StubUtils.setFailedStatus(confirmedRecord);
				result.getGenericReplicatedRecord().add(confirmedRecord);
			}
		}
		service.sendResults(result);
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord",
				String.format("select * from QueuedRecord where entityWillowId = 1 or entityWillowId = 1482"));
		assertEquals("Expecting two queuable records.", actualData.getRowCount(), 2);
		for (int i = 0; i < actualData.getRowCount(); i++) {
			Integer numberOfAttempts = (Integer) actualData.getValue(i, "numberOfAttempts");
			assertTrue("Expecting numberOfAttempts=1.", numberOfAttempts.equals(1));
		}
	}
	
	@Test
	public void testV5SendResultsStatusFail() throws Exception {
		IReplicationService service = getService(IReplicationService.class);
		GenericReplicationResult result = PortHelper.createReplicationResult(SupportedVersions.V5);
		GenericReplicationRecords replicatedRecords = service.getRecords(SupportedVersions.V5);
		for (GenericTransactionGroup group : replicatedRecords.getGenericGroups()) {
			for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
				GenericReplicatedRecord confirmedRecord = ReplicationUtils.toReplicatedRecord(stub, true);
				confirmedRecord.setMessage("Record replicated.");
				StubUtils.setFailedStatus(confirmedRecord);
				result.getGenericReplicatedRecord().add(confirmedRecord);
			}
		}
		service.sendResults(result);
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
