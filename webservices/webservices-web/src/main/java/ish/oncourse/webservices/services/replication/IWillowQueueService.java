package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;

import java.util.List;

public interface IWillowQueueService {
	List<QueuedRecord> getReplicationQueue();

	Queueable findRelatedEntity(QueuedRecord entity);

	void confirmRecord(Long willowId, Long angelId, String entityName, boolean isSuccess);
}
