package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.v4.stubs.replication.ReplicationRequest;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;

import javax.jws.WebService;

@WebService(
	endpointInterface = "ish.oncourse.webservices.soap.v4.ReplicationPortType",
	serviceName = "ReplicationService",
	portName = "ReplicationPort", targetNamespace="http://repl.v4.soap.webservices.oncourse.ish/")
		
public class ReplicationPortTypeImpl implements ReplicationPortType {

	public ReplicationResult sendRecords(ReplicationRequest req) {
		ReplicationResult result = new ReplicationResult();
		return result;
	}

	public ReplicationResult getRecords() {
		ReplicationResult result = new ReplicationResult();
		return result;
	}
}
