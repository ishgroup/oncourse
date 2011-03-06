package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.replication.IWillowStubBuilder;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.services.replication.WillowStubBuilderFactory;
import ish.oncourse.webservices.services.replication.WillowUpdaterFactory;
import ish.oncourse.webservices.updaters.replication.IWillowUpdater;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
	public ReplicationResult sendRecords(ReplicationRecords req) {

		ReplicationResult result = new ReplicationResult();
		List<ReplicatedRecord> replicatedRecords = new LinkedList<ReplicatedRecord>();

		@SuppressWarnings("rawtypes")
		IWillowUpdater updater = updaterFactory.newReplicationUpdater();

		for (ReplicationStub stub : req.getAttendanceOrBinaryDataOrBinaryInfo()) {
			replicatedRecords.addAll(updater.updateRecord(stub));
		}

		result.getReplicatedRecord().addAll(replicatedRecords);

		return result;
	}

	@Override
	public ReplicationRecords getRecords() {

		ReplicationRecords result = new ReplicationRecords();

		LinkedHashMap<QueuedKey, QueuedRecord> queue = mapQueue(queueService.getReplicationQueue());

		List<ReplicationStub> records = result.getAttendanceOrBinaryDataOrBinaryInfo();

		IWillowStubBuilder builder = stubBuilderFactory.newReplicationStubBuilder(queue);

		while (!queue.isEmpty()) {
			QueuedKey key = queue.keySet().iterator().next();
			records.add(builder.convert(queue.get(key)));
			queue.remove(key);
		}

		return result;
	}

	@Override
	public void sendResults(ReplicationResult request) {
		for (ReplicatedRecord record : request.getReplicatedRecord()) {
			queueService.confirmRecord(record.getStub().getWillowId(), record.getStub().getAngelId(), record.getStub().getEntityIdentifier(),
					record.getStatus() == Status.SUCCESS);
		}
	}

	private static LinkedHashMap<QueuedKey, QueuedRecord> mapQueue(List<QueuedRecord> queue) {
		LinkedHashMap<QueuedKey, QueuedRecord> mappedQueue = new LinkedHashMap<QueuedKey, QueuedRecord>();

		for (QueuedRecord r : queue) {
			mappedQueue.put(new QueuedKey(r.getEntityWillowId(), r.getEntityIdentifier()), r);
		}

		return mappedQueue;
	}
}
