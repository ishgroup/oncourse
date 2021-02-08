/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Checkout
import ish.oncourse.webservices.v23.stubs.replication.CheckoutStub

class CheckoutUpdater extends AbstractAngelUpdater<CheckoutStub, Checkout> {
    @Override
    protected void updateEntity(CheckoutStub stub, Checkout entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setUUID(stub.getCartUUID())
        entity.setShoppingCart(stub.getShoppingCart())
    }
}
