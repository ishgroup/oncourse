package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseCourseRelation;
import ish.oncourse.model.EntityRelation;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v7.stubs.replication.EntityRelationStub;

public class CourseCourseRelationUpdater extends EntityRelationUpdater {

	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		if (entity instanceof CourseCourseRelation) {
			CourseCourseRelation relationEntity = (CourseCourseRelation) entity;
			relationEntity.setToCourse((Course) callback.updateRelationShip(stub.getToEntityAngelId(), ENTITY_CLASS_MAPPING.get(entity.getToEntityIdentifier())));
			relationEntity.setFromCourse((Course) callback.updateRelationShip(stub.getFromEntityAngelId(), ENTITY_CLASS_MAPPING.get(entity.getFromEntityIdentifier())));
		} else {
			throw new UpdaterException("Invalid entity type passed!" + entity.getClass());
		}
	}
	
}
