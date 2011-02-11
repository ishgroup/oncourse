package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.services.replication.IReplicationQueueService;
import ish.oncourse.webservices.services.replication.ReplicationStubBuilderFactory;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRequest;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.List;
import java.util.SortedMap;

import javax.jws.WebService;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReplicationPortType", serviceName = "ReplicationService", portName = "ReplicationPort", targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/")
public class ReplicationPortTypeImpl implements ReplicationPortType {

	@Inject
	@Autowired
	private IReplicationQueueService queueService;

	@Inject
	@Autowired
	private ReplicationStubBuilderFactory stubBuilderFactory;

	@Override
	public ReplicationResult sendRecords(ReplicationRequest req) {
		ReplicationResult result = new ReplicationResult();
		return result;
	}

	@Override
	public ReplicationResult getRecords() {
		ReplicationResult result = new ReplicationResult();

		SortedMap<QueuedKey, QueuedRecord> queue = queueService.getReplicationQueue();
		
		List<ReplicationStub> records = result.getAttendanceOrBinaryDataOrBinaryInfo();

		IReplicationStubBuilder builder = stubBuilderFactory.newReplicationStubBuilder(queue);

		while (!queue.isEmpty()) {
			QueuedKey key = queue.firstKey();
			records.add(builder.convert(queue.get(key)));
			queue.remove(key);
		}

		return result;
	}
}
