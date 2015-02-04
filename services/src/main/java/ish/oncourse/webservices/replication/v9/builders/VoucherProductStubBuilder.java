/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.v9.stubs.replication.VoucherProductStub;

public class VoucherProductStubBuilder extends AbstractProductStubBuilder<VoucherProduct, VoucherProductStub> {

	@Override
	protected VoucherProductStub createFullStub(VoucherProduct entity) {
		VoucherProductStub stub = super.createFullStub(entity);
		stub.setExpiryDays(entity.getExpiryDays());
		stub.setExpiryType(entity.getExpiryType().getDatabaseValue());
		stub.setMaxCoursesRedemption(entity.getMaxCoursesRedemption());
		if (entity.getValue() != null) {
			stub.setValue(entity.getValue().toBigDecimal());
		}
		return stub;
	}

	@Override
	protected VoucherProductStub createStub() {
		return new VoucherProductStub();
	}
}
