package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedRecord;

import java.util.List;

public interface IWillowQueueService {
	List<QueuedRecord> getReplicationQueue();
}
