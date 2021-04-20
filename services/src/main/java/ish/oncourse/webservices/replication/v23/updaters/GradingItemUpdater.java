package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.GradingItem;
import ish.oncourse.model.GradingType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.GradingItemStub;

public class GradingItemUpdater extends AbstractWillowUpdater<GradingItemStub, GradingItem> {
    @Override
    protected void updateEntity(GradingItemStub stub, GradingItem entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setItemName(stub.getName());
        entity.setLowerBound(stub.getLowerBound());
        entity.setGradingType(callback.updateRelationShip(stub.getGradingTypeId(), GradingType.class));
    }
}
