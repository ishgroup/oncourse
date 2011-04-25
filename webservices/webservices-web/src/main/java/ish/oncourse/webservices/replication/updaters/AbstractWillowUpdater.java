package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public abstract class AbstractWillowUpdater<V extends ReplicationStub, T extends Queueable> implements IWillowUpdater {
	/**
	 * @see ish.oncourse.server.replication.updater.IAngelUpdater#updateEntityFromStub(ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable,
	 *      ish.oncourse.server.replication.updater.RelationShipCallback)
	 */
	@SuppressWarnings("unchecked")
	public void updateEntityFromStub(ReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		updateEntity((V) stub, (T) entity, callback);
	}

	protected abstract void updateEntity(V stub, T entity, RelationShipCallback callback);
}
