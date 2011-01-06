package ish.oncourse.webservices.soap;

import javax.jws.WebService;

import ish.oncourse.webservices.soap.stubs.replication.ReplicationRequest;
import ish.oncourse.webservices.soap.stubs.replication.ReplicationResult;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.ReplicationPortType",
		serviceName = "ReplicationService",
		portName = "ReplicationPort")
		
public class ReplicationPortTypeImpl implements ReplicationPortType {

	@Override
	public ReplicationResult sendRecords(ReplicationRequest req) {
		ReplicationResult result = new ReplicationResult();
		return result;
	}

	@Override
	public ReplicationResult getRecords() {
		ReplicationResult result = new ReplicationResult();
		return result;
	}
}
