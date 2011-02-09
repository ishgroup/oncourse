package ish.oncourse.webservices.builders;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public interface IReplicationStubBuilder {
	ReplicationStub convert(QueuedRecord entity);
}
