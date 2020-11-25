package ish.oncourse.webservices.replication.v23.updaters;

import ish.common.types.EntityRelationCartAction;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Discount;
import ish.oncourse.model.EntityRelationType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationTypeStub;

public class EntityRelationTypeUpdater extends AbstractWillowUpdater<EntityRelationTypeStub, EntityRelationType> {
    @Override
    protected void updateEntity(EntityRelationTypeStub stub, EntityRelationType entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setName(stub.getName());
        entity.setToName(stub.getToName());
        entity.setFromName(stub.getFromName());
        entity.setDescription(stub.getDescription());
        entity.setIsShownOnWeb(stub.isIsShownOnWeb());
        entity.setConsiderHistory(stub.isConsiderHistory());
        entity.setShoppingCart(TypesUtil.getEnumForDatabaseValue(stub.getShoppingCart(), EntityRelationCartAction.class));
        entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
    }
}
