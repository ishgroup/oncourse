package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public interface IWillowStubBuilder {
	ReplicationStub convert(QueuedRecord entity);
}
