package ish.oncourse.model;

import ish.oncourse.model.auto._QueuedRecord;

public class QueuedRecord extends _QueuedRecord {
	
	public QueuedRecord() {
		super();
	}
	
	public QueuedRecord(QueuedRecordAction action, String entityIdentifier, Long entityWillowId) {
		setAction(action);
		setEntityIdentifier(entityIdentifier);
		setEntityWillowId(entityWillowId);
	}
}
