package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedRecord;

import java.util.SortedSet;
import java.util.TreeSet;

public class DFADeduper {

	private SortedSet<String> transactionKeys = new TreeSet<String>();

	public static enum InternalState {
		DFA_CREATE, DFA_UPDATE, DFA_DELETE, DFA_NOP
	};

	private InternalState currentState;
	private QueuedRecord currentRecord;

	public void nextState(QueuedRecord record) throws DeduperException {
		transactionKeys.add(record.getTransactionKey());

		if (currentRecord != null) {
			if (!currentRecord.getEntityIdentifier().equals(record.getEntityIdentifier())
					|| !currentRecord.getEntityWillowId().equals(record.getEntityWillowId())) {
				throw new DeduperException(String.format("Expecting entity:%s, with id:%s, but got entity:%s with id:%s",
						currentRecord.getEntityIdentifier(), currentRecord.getEntityWillowId(), record.getEntityIdentifier(),
						record.getEntityWillowId()));
			}
		}

		this.currentRecord = record;

		switch (currentState) {
		case DFA_CREATE: {
			switch (record.getAction()) {
			case CREATE:
				throw new DeduperException("Can't accept secod CREATE event.");
			case UPDATE:
				break;
			case DELETE:
				this.currentState = InternalState.DFA_NOP;
				break;
			}
			break;
		}
		case DFA_UPDATE: {
			switch (record.getAction()) {
			case CREATE:
				throw new DeduperException("Can't accept CREATE event after UPDATE event.");
			case UPDATE:
				break;
			case DELETE:
				this.currentState = InternalState.DFA_DELETE;
				break;
			}
			break;
		}
		case DFA_DELETE: {
			throw new DeduperException("Can't accept any events after DELETE event.");
		}
		case DFA_NOP: {
			throw new DeduperException("Can't accept any events in NOP state.");
		}
		default: {
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
		}
		}
	}

	public SortedSet<String> getTransactionKeys() {
		return transactionKeys;
	}

	public QueuedRecord getCurrentRecord() {
		if (currentState == InternalState.DFA_NOP) {
			return null;
		}
		return currentRecord;
	}
}
