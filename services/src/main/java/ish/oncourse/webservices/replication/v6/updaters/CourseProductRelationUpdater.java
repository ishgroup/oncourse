/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseProductRelation;
import ish.oncourse.model.EntityRelation;
import ish.oncourse.model.Product;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v6.stubs.replication.EntityRelationStub;


public class CourseProductRelationUpdater extends EntityRelationUpdater {

	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		if (entity instanceof CourseProductRelation) {
			CourseProductRelation relationEntity = (CourseProductRelation) entity;
			relationEntity.setCourse((Course) callback.updateRelationShip(stub.getFromEntityAngelId(), ENTITY_CLASS_MAPPING.get(entity.getFromEntityIdentifier())));
			relationEntity.setProduct((Product) callback.updateRelationShip(stub.getToEntityAngelId(), ENTITY_CLASS_MAPPING.get(entity.getToEntityIdentifier())));
		} else {
			throw new UpdaterException("Invalid entity type passed!" + entity.getClass());
		}
	}
}
