package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.soap.v4.ReplicationService;
import ish.oncourse.webservices.v4.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
public class ReplicationPortTypeTransportTest extends AbstractTransportTest {

	@Test
	public void testReplicationPortType_authenticate() throws Exception {

		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.authenticate("securityCode", Long.MAX_VALUE);
	}

	@Test
	public void testReplicationPortType_confirmExecution() throws Exception {

		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.confirmExecution(Long.MAX_VALUE, "confirmExecution");
	}

	@Test
	public void testReplicationPortType_getRecords() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		ReplicationRecords replicationRecords = replicationPortType.getRecords();
		assertNotNull(replicationRecords);
		TransactionGroup transactionGroup = replicationRecords.getGroups().get(0);
		assertTransactionGroup(transactionGroup);
	}

	@Test
	public void testReplicationPortType_logout() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.logout(Long.MAX_VALUE);
	}

	@Test
	public void testReplicationPortType_sendRecords() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		ReplicationRecords replicationRecords = new ReplicationRecords();
		ReplicationResult replicationResult = replicationPortType.sendRecords(replicationRecords);
		assertNotNull(replicationResult);
		assertEquals(1,replicationResult.getReplicatedRecord().size());
	}

	@Test
	public void testReplicationPortType_sendResults() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		ReplicationResult replicationRecords = new ReplicationResult();
		int result = replicationPortType.sendResults(replicationRecords);
		assertEquals(Integer.MAX_VALUE,result);
	}

	@Test
	public void testReplicationPortType_getInstructions() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		List<InstructionStub> result = replicationPortType.getInstructions();
		assertNotNull(result);
	}

	private ReplicationPortType getReplicationPortType() throws JAXBException {
		ReplicationService replicationService = new ReplicationService(ReplicationPortType.class.getClassLoader().getResource("wsdl/v4_replication.wsdl"));
		ReplicationPortType replicationPortType = replicationService.getReplicationPort();

		initPortType((BindingProvider) replicationPortType, "/services/v4/replication");
		return replicationPortType;
	}


}
