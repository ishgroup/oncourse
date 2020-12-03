/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.webservices.v23.stubs.replication.VoucherStub

/**
 */
class VoucherStubBuilder extends AbstractProductItemStubBuilder<Voucher, VoucherStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected VoucherStub createFullStub(final Voucher entity) {
		final VoucherStub stub = super.createFullStub(entity)

		stub.setCode(entity.getCode())
		stub.setRedeemedCoursesCount(entity.getRedeemedCourseCount())
		stub.setSource(entity.getSource().getDatabaseValue())
		stub.setExpiryDate(entity.getExpiryDate())

		if (entity.getRedeemableBy() != null) {
			stub.setContactId(entity.getRedeemableBy().getId())
		}

		if (entity.getRedemptionValue() != null) {
			stub.setRedemptionValue(entity.getRedemptionValue().toBigDecimal())
		}

		if (entity.getValueOnPurchase() != null) {
			stub.setValueOnPurchase(entity.getValueOnPurchase().toBigDecimal())
		}
		return stub
	}

	@Override
	protected VoucherStub createStub() {
		return new VoucherStub()
	}

}
