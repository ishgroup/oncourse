/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.replication.updaters

import ish.common.types.TypesUtil
import ish.common.types.VoucherPaymentStatus
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherPaymentIn
import ish.oncourse.webservices.v21.stubs.replication.VoucherPaymentInStub

/**
 */
class VoucherPaymentInUpdater extends AbstractAngelUpdater<VoucherPaymentInStub, VoucherPaymentIn> {

	@Override
	protected void updateEntity(VoucherPaymentInStub stub, VoucherPaymentIn entity, RelationShipCallback callback) {
		entity.setEnrolmentsCount(stub.getEnrolmentsCount())
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), VoucherPaymentStatus.class))
		entity.setVoucher(callback.updateRelationShip(stub.getVoucherId(), Voucher.class))
		entity.setPaymentIn(callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class))
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class))
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
	}
}
