package ish.oncourse.webservices.services.replication;

import java.util.List;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public class ReplicationStubBuilder {
	
	private List<QueuedRecord> queue;
	
	public ReplicationStubBuilder(List<QueuedRecord> queue) {
		
	}
	
	public ReplicationStub convert(QueuedRecord record) {
		return null;
	}
}
