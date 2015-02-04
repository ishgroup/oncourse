package ish.oncourse.webservices.soap.v9;

import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.soap.v9.AuthFailure;
import ish.oncourse.webservices.soap.v9.ReplicationFault;
import ish.oncourse.webservices.soap.v9.ReplicationPortType;
import ish.oncourse.webservices.v9.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v9.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v9.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v9.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v9.stubs.replication.SiteStub;
import ish.oncourse.webservices.v9.stubs.replication.TransactionGroup;
import ish.oncourse.webservices.v9.stubs.replication.UnreplicatedEntitiesStub;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@WebService(endpointInterface = "ish.oncourse.webservices.soap.v9.ReplicationPortType", serviceName = "ReplicationService", portName = "ReplicationPort", targetNamespace = "http://repl.v9.soap.webservices.oncourse.ish/")
public class TestReplicationPortTypeImpl implements ReplicationPortType {

	@Override
	public ReplicationResult sendRecords(@WebParam(partName = "records", name = "records", targetNamespace = "") ReplicationRecords replicationRecords) throws ReplicationFault {
		assertNotNull(replicationRecords);
		ReplicationResult replicationResult = new ReplicationResult();
		replicationResult.getReplicatedRecord().add((ReplicatedRecord) ReplicationUtils.toReplicatedRecord(new SiteStub(), true));
		return replicationResult;
	}

	@Override
	public void confirmExecution(@WebParam(name = "arg0", targetNamespace = "") Long aLong, @WebParam(name = "arg1", targetNamespace = "") String s) {
		assertEquals(Long.MAX_VALUE, aLong.longValue());
		assertEquals(Long.MAX_VALUE, "confirmExecution");
	}

	@Override
	public ReplicationRecords getRecords() throws ReplicationFault {

		TransactionGroup transactionGroup = null;
		try {
			transactionGroup = AbstractTransportTest.createTransactionGroupWithAllStubs();
		} catch (Throwable throwable) {
			throw new ReplicationFault("",throwable);
		}

		ReplicationRecords replicationRecords = new ReplicationRecords();
		replicationRecords.getGroups().add(transactionGroup);

		return replicationRecords;
	}

	@Override
	public List<UnreplicatedEntitiesStub> getUnreplicatedEntities() {
		return new ArrayList<>();
	}

	//@Override
	public void logout(@WebParam(partName = "communicationKey", name = "communicationKey", targetNamespace = "") long l) {
		assertEquals(Long.MAX_VALUE, l);
	}

	@Override
	public List<InstructionStub> getInstructions() {
		return new ArrayList<InstructionStub>();
	}

	@Override
	public int sendResults(@WebParam(partName = "replResult", name = "replResult", targetNamespace = "") ReplicationResult replicationResult) throws ReplicationFault {
		return Integer.MAX_VALUE;
	}

	@Override
	public long authenticate(@WebParam(partName = "securityCode", name = "securityCode", targetNamespace = "") String s,
							 @WebParam(partName = "lastCommunicationKey", name = "lastCommunicationKey", targetNamespace = "") long l) throws AuthFailure {
		assertEquals("securityCode", s);
		assertEquals(Long.MAX_VALUE, l);
		return Long.MAX_VALUE;
	}
}
