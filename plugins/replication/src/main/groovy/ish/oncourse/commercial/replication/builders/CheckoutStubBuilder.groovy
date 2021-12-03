/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Checkout
import ish.oncourse.webservices.v25.stubs.replication.CheckoutStub

@CompileStatic
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
