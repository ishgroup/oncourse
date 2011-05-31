package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public interface IWillowUpdater {
	void updateEntityFromStub(ReplicationStub stub, Queueable entity, RelationShipCallback callback);
}
