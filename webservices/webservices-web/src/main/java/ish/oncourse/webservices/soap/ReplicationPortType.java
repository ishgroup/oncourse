package ish.oncourse.webservices.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import ish.oncourse.webservices.soap.stubs.replication.ReplicationRequest;
import ish.oncourse.webservices.soap.stubs.replication.ReplicationResult;

@WebService
public interface ReplicationPortType {

	@WebMethod(operationName = "sendRecords")
	ReplicationResult sendRecords(@WebParam(name = "req") ReplicationRequest req);

	@WebMethod(operationName = "getRecords")
	ReplicationResult getRecords();
}
