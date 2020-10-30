/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.webservices.v22.stubs.replication.PaymentInLineStub

/**
 */
class PaymentInLineStubBuilder extends AbstractAngelStubBuilder<PaymentInLine, PaymentInLineStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected PaymentInLineStub createFullStub(PaymentInLine entity) {
		def stub = new PaymentInLineStub()
		stub.setAmount(entity.getAmount().toBigDecimal())
		stub.setCreated(entity.getCreatedOn())
		stub.setInvoiceId(entity.getInvoice().getId())
		stub.setModified(entity.getModifiedOn())
		stub.setPaymentInId(entity.getPaymentIn().getId())
		return stub
	}

}
