package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;

import java.util.List;

public interface IWillowQueueService {
	List<QueuedRecord> getReplicationQueue(int limit);
	void cleanEmptyTransactions();
}
