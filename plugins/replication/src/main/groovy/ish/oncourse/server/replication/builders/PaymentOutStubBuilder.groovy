/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.common.types.PaymentSource
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.webservices.v21.stubs.replication.PaymentOutStub
import ish.util.LocalDateUtils

/**
 */
class PaymentOutStubBuilder extends AbstractAngelStubBuilder<PaymentOut, PaymentOutStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected PaymentOutStub createFullStub(PaymentOut entity) {
		def stub = new PaymentOutStub()
		stub.setAmount(entity.getAmount().toBigDecimal())
		stub.setContactId(entity.getPayee().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setPaymentInTxnReference(entity.getPaymentInGatewayReference())
		stub.setStatus(entity.getStatus().getDatabaseValue())
		stub.setType(entity.getPaymentMethod().getType().getDatabaseValue())
		// There is no source field in paymentOut in angel.
		stub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue())
		stub.setDateBanked(LocalDateUtils.valueToDate(entity.getDateBanked()))
		stub.setDatePaid(LocalDateUtils.valueToDate(entity.getPaymentDate()))
		if (entity.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue())
		}
		return stub
	}
}
