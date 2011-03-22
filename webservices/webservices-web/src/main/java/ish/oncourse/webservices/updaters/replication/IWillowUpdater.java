package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.List;

public interface IWillowUpdater<V extends ReplicationStub> {
	List<ReplicatedRecord> updateRecord(V stub);
}
