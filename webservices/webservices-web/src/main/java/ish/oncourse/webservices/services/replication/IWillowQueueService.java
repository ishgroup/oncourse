package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;

import java.util.List;

public interface IWillowQueueService {
	List<QueuedRecord> getReplicationQueue();

	Queueable findRelatedEntity(String entityIdentifier, Long willowId);
	
	Class<? extends Queueable> getEntityClass(String entityIdentifier);
	
	<T extends Queueable> void remove(T object);
	
	<T extends Queueable> T update(T object);
	
	<T extends Queueable> T createNew(Class<T> clazz);

	void confirmRecord(Long willowId, Long angelId, String entityName, boolean isSuccess);
}
