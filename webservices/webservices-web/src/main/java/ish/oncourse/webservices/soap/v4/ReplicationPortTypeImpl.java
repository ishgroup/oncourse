package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.replication.IWillowStubBuilder;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.services.replication.WillowStubBuilderFactory;
import ish.oncourse.webservices.services.replication.WillowUpdaterFactory;
import ish.oncourse.webservices.updaters.replication.IWillowUpdater;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
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
	private IWillowQueueService queueService;

	@Inject
	@Autowired
	private WillowStubBuilderFactory stubBuilderFactory;
	
	@Inject
	@Autowired
	private WillowUpdaterFactory updaterFactory;

	@Override
	public ReplicationResult sendRecords(ReplicationRequest req) {
		
		ReplicationResult result = new ReplicationResult();
		List<ReplicationStub> stubs = req.getAttendanceOrBinaryDataOrBinaryInfo();
		
		@SuppressWarnings("rawtypes")
		IWillowUpdater updater = updaterFactory.newReplicationUpdater();
		
		for (ReplicationStub stub : stubs) {
			@SuppressWarnings("unchecked")
			List<HollowStub> hollowStubs = updater.updateRecord(stub);
			stubs.addAll(hollowStubs);
		}
		
		return result;
	}

	@Override
	public ReplicationResult getRecords() {
		ReplicationResult result = new ReplicationResult();

		SortedMap<QueuedKey, QueuedRecord> queue = queueService.getReplicationQueue();
		
		List<ReplicationStub> records = result.getAttendanceOrBinaryDataOrBinaryInfo();

		IWillowStubBuilder builder = stubBuilderFactory.newReplicationStubBuilder(queue);

		while (!queue.isEmpty()) {
			QueuedKey key = queue.firstKey();
			records.add(builder.convert(queue.get(key)));
			queue.remove(key);
		}

		return result;
	}

	@Override
	public ReplicationResult sendResults(ReplicationRequest records) {
		// TODO Auto-generated method stub
		return null;
	}
}
