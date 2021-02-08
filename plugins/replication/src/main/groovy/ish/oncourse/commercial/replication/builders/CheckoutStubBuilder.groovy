/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Checkout
import ish.oncourse.webservices.v23.stubs.replication.CheckoutStub
import ish.util.LocalDateUtils

class CheckoutStubBuilder extends AbstractAngelStubBuilder<Checkout, CheckoutStub> {

    /**
     * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
     */
    @Override
    protected CheckoutStub createFullStub(Checkout entity) {
        CheckoutStub stub = new CheckoutStub()

        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setCartUUID(entity.getUUID())
        stub.setShoppingCart(entity.getShoppingCart())

        return stub
    }
}
