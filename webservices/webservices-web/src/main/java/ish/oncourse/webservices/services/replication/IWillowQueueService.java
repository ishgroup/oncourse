package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;

import java.util.SortedMap;

public interface IWillowQueueService {
	SortedMap<QueuedKey, QueuedRecord> getReplicationQueue();
	QueuedRecord find(Long willowId, String entityName);
}
