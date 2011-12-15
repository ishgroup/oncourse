package ish.oncourse.model;

import ish.oncourse.model.auto._QueuedTransaction;

public class QueuedTransaction extends _QueuedTransaction {
	
	private static final long serialVersionUID = -1318051832105725074L;
	
	/**
	 * Check if any record within transaction has reached max retry level.
	 * 
	 * @return true/false
	 */
	public boolean shouldSkipTransaction() {

		for (QueuedRecord r : getQueuedRecords()) {
			if (r.getNumberOfAttempts() >= QueuedRecord.MAX_NUMBER_OF_RETRY) {
				return true;
			}
		}

		return false;
	}
}
