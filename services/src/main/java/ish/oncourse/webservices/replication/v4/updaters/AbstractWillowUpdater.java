package ish.oncourse.webservices.replication.v4.updaters;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.util.GenericReplicationStub;

public abstract class AbstractWillowUpdater<V extends GenericReplicationStub, T extends Queueable> implements IWillowUpdater {
	protected static final Logger LOG = Logger.getLogger(AbstractWillowUpdater.class);
	/**
	 * @see ish.oncourse.server.replication.updater.IAngelUpdater#updateEntityFromStub(ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable,
	 *      ish.oncourse.server.replication.updater.RelationShipCallback)
	 */
	@SuppressWarnings("unchecked")
	public void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		updateEntity((V) stub, (T) entity, callback);
	}
	
	protected final String getCurrentCollegeAngelVersion(Queueable entity) {
		return entity.getCollege() != null && StringUtils.trimToNull(entity.getCollege().getAngelVersion()) != null ? 
			entity.getCollege().getAngelVersion() : College.UNDEFINED_ANGEL_VERSION;
	}

	protected abstract void updateEntity(V stub, T entity, RelationShipCallback callback);
}
