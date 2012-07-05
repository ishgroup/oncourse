package ish.oncourse.webservices.replication.v4.updaters;

import ish.oncourse.model.Queueable;

public interface RelationShipCallback {
	<M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz);
}
