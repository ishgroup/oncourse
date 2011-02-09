package ish.oncourse.webservices.soap.v4;

import java.util.ArrayList;
import java.util.List;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IQueuedRecordService;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRequest;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import javax.jws.WebService;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(
	endpointInterface = "ish.oncourse.webservices.soap.v4.ReplicationPortType",
	serviceName = "ReplicationService",
	portName = "ReplicationPort", targetNamespace="http://repl.v4.soap.webservices.oncourse.ish/")
		
public class ReplicationPortTypeImpl implements ReplicationPortType {
	
	@Inject
	@Autowired
	private IQueuedRecordService queueService;
	
	
	public ReplicationResult sendRecords(ReplicationRequest req) {
		ReplicationResult result = new ReplicationResult();
		return result;
	}

	public ReplicationResult getRecords() {
		ReplicationResult result = new ReplicationResult();
		
		List<QueuedRecord> queue = queueService.getRecords();
		List<ReplicationStub> records = new ArrayList<ReplicationStub>();
		
		while (!queue.isEmpty()) {
			
		}
		
		result.getAttendanceOrBinaryDataOrBinaryInfo().addAll(records);
		
		return result;
	}
}
