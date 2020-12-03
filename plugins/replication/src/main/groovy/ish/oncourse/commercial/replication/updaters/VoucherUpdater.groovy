/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.PaymentSource
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.webservices.v23.stubs.replication.VoucherStub

/**
 */
class VoucherUpdater extends AbstractProductItemUpdater<VoucherStub, Voucher> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(final VoucherStub stub, final Voucher entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback)

		entity.setProduct(callback.updateRelationShip(stub.getProductId(), VoucherProduct.class))
		entity.setCode(stub.getCode())
		entity.setRedeemableBy(callback.updateRelationShip(stub.getContactId(), Contact.class))
		entity.setRedeemedCourseCount(stub.getRedeemedCoursesCount())
		entity.setRedemptionValue(stub.getRedemptionValue() != null ? new Money(stub.getRedemptionValue()) : null)
		entity.setValueOnPurchase(stub.getValueOnPurchase() != null ? new Money(stub.getValueOnPurchase()) : null)
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class))
		entity.setExpiryDate(stub.getExpiryDate())
	}

}
