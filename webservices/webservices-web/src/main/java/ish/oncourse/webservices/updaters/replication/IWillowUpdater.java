package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.List;

public interface IWillowUpdater<V extends ReplicationStub> {
	Queueable updateRecord(V stub, List<ReplicatedRecord> result);
}
