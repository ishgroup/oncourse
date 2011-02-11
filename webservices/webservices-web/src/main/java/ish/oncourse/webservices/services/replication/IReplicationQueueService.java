package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;

import java.util.SortedMap;

public interface IReplicationQueueService {
	SortedMap<QueuedKey, QueuedRecord> getReplicationQueue();
}
