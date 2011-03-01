package ish.oncourse.webservices.updaters.replication;

import java.util.List;

import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public interface IWillowUpdater<V extends ReplicationStub> {
	List<HollowStub> updateRecord(V stub);
}
