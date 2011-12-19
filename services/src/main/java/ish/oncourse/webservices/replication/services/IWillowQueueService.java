package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedTransaction;

import java.util.List;

public interface IWillowQueueService {
	
	/**
	 * Get size of current queue.
	 * 
	 * @return queue size
	 */
	int getNumberOfTransactions();
	
	/**
	 * Gets replication records starting from transaction with index, the number of records is limit.
	 * 
	 * @param fromTransaction starting index
	 * @param numberOfTransactions number of transactions
	 * @return queued transactions
	 */
	List<QueuedTransaction> getReplicationQueue(int fromTransaction, int numberOfTransactions);
	
	/**
	 * Removes empty transactions from the queue.
	 */
	void cleanEmptyTransactions();
}
