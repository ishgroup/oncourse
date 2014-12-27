package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.util.GenericReplicationStub;

public interface IWillowUpdater {
	void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback);
}
