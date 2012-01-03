package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class DFADedupper implements Comparable<DFADedupper> {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(DFADedupper.class);

	/**
	 * Transaction keys
	 */
	private SortedSet<String> transactionKeys = new TreeSet<String>();

	/**
	 * Deduplication states
	 * @author anton
	 *
	 */
	public static enum InternalState {
		DFA_START, DFA_CREATE, DFA_UPDATE, DFA_DELETE, DFA_NOP
	};

	/**
	 * Current state
	 */
	private InternalState currentState = InternalState.DFA_START;

	/**
	 * Recordset
	 */
	private LinkedList<QueuedRecord> recordSet = new LinkedList<QueuedRecord>();

	public void nextState(QueuedRecord record) throws DedupperException {
		transactionKeys.add(record.getQueuedTransaction().getTransactionKey());

		QueuedRecord currentRecord = (recordSet.isEmpty()) ? null : recordSet.getLast();

		if (currentRecord != null) {
			if (!currentRecord.getEntityIdentifier().equals(record.getEntityIdentifier())
					|| !currentRecord.getEntityWillowId().equals(record.getEntityWillowId())) {
				throw new DedupperException(String.format("Expecting entity:%s, with id:%s, but got entity:%s with id:%s",
						currentRecord.getEntityIdentifier(), currentRecord.getEntityWillowId(), record.getEntityIdentifier(),
						record.getEntityWillowId()));
			}
		}

		recordSet.add(record);

		switch (currentState) {
		case DFA_START:
			switch (record.getAction()) {
			case CREATE:
				this.currentState = InternalState.DFA_CREATE;
				break;
			case UPDATE:
				this.currentState = InternalState.DFA_UPDATE;
				break;
			case DELETE:
				this.currentState = InternalState.DFA_DELETE;
				break;
			}
			break;
		case DFA_CREATE:
			switch (record.getAction()) {
			case CREATE:
				throw new DedupperException("Can't accept secod CREATE event.");
			case UPDATE:
				break;
			case DELETE:
				this.currentState = InternalState.DFA_NOP;
				break;
			}
			break;
		case DFA_UPDATE:
			switch (record.getAction()) {
			case CREATE:
				logger.warn(String.format("Accept secod CREATE event for entity:%s with id:%s.", record.getEntityIdentifier(),
						record.getEntityWillowId()));
				break;
			case UPDATE:
				break;
			case DELETE:
				this.currentState = InternalState.DFA_DELETE;
				break;
			}
			break;
		case DFA_DELETE:
			throw new DedupperException("Can't accept any events after DELETE event.");
		case DFA_NOP:
			throw new DedupperException("Can't accept any events in NOP state.");
		}
	}

	public SortedSet<String> getTransactionKeys() {
		return transactionKeys;
	}

	@Override
	public int compareTo(DFADedupper o) {
		return getTransactionKeys().size() - o.getTransactionKeys().size();
	}

	public QueuedRecord deDuppedRecord() {
		if (currentState == InternalState.DFA_NOP) {
			return null;
		}
		return recordSet.getLast();
	}

	public List<QueuedRecord> duplicates() {
		QueuedRecord dedupped = deDuppedRecord();
		if (dedupped != null) {
			return recordSet.subList(0, recordSet.size() - 1);
		}
		return recordSet;
	}
}
