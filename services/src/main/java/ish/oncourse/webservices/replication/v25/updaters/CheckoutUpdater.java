package ish.oncourse.webservices.replication.v25.updaters;

import ish.oncourse.model.Checkout;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v25.stubs.replication.CheckoutStub;

public class CheckoutUpdater extends AbstractWillowUpdater<CheckoutStub, Checkout> {
    @Override
    protected void updateEntity(CheckoutStub stub, Checkout entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setUUID(stub.getCartUUID());
        entity.setShoppingCart(stub.getShoppingCart());
    }
}
