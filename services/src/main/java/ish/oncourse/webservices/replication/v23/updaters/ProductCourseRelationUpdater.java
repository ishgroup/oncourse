package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationStub;

public class ProductCourseRelationUpdater extends EntityRelationUpdater {

    @Override
    protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
        super.updateEntity(stub, entity, callback);
        if (entity instanceof ProductCourseRelation) {
            ProductCourseRelation relationEntity = (ProductCourseRelation) entity;
            relationEntity.setToCourse((Course) callback.updateRelationShip(stub.getFromEntityAngelId(), ENTITY_CLASS_MAPPING.get(entity.getFromEntityIdentifier())));
            relationEntity.setFromProduct((Product) callback.updateRelationShip(stub.getToEntityAngelId(), ENTITY_CLASS_MAPPING.get(entity.getToEntityIdentifier())));
        } else {
            throw new UpdaterException("Invalid entity type passed!" + entity.getClass());
        }
    }
}
