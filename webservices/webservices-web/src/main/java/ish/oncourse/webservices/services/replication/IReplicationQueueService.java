package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;

import java.util.Map;

public interface IReplicationQueueService {
	Map<QueuedKey, QueuedRecord> getReplicationQueue();
}
