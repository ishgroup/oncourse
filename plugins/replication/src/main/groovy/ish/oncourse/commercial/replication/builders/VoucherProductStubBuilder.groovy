/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.webservices.v23.stubs.replication.VoucherProductStub

/**
 */
class VoucherProductStubBuilder extends AbstractProductStubBuilder<VoucherProduct, VoucherProductStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
    @Override
    protected VoucherProductStub createFullStub(final VoucherProduct entity) {
        final def stub = super.createFullStub(entity) as VoucherProductStub

        stub.setExpiryDays(entity.getExpiryDays())
        stub.setExpiryType(entity.getExpiryType().getDatabaseValue())
        stub.setMaxCoursesRedemption(entity.getMaxCoursesRedemption())
        if (entity.getValue() != null) {
            stub.setValue(entity.getValue().toBigDecimal())
        }

        return stub
    }

    @Override
    protected VoucherProductStub createStub() {
        return new VoucherProductStub()
    }
}
