package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.Checkout;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.CheckoutStub;

public class CheckoutStubBuilder extends AbstractWillowStubBuilder<Checkout, CheckoutStub> {
    @Override
    protected CheckoutStub createFullStub(Checkout entity) {
        CheckoutStub stub = new CheckoutStub();

        stub.setCreated(entity.getCreated());
        stub.setModified(entity.getModified());
        stub.setCartUUID(entity.getUUID());
        stub.setShoppingCart(entity.getShoppingCart());

        return stub;
    }
}
