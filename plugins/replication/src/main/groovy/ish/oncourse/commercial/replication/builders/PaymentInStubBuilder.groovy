/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.webservices.v23.stubs.replication.PaymentInStub
import ish.util.LocalDateUtils

/**
 */
class PaymentInStubBuilder extends AbstractAngelStubBuilder<PaymentIn, PaymentInStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected PaymentInStub createFullStub(PaymentIn entity) {
		def stub = new PaymentInStub()
		stub.setAmount(entity.getAmount().toBigDecimal())
		stub.setContactId(entity.getPayer().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setPrivateNotes(entity.getPrivateNotes())
		// stub.setGatewayReference(entity.getGatewayReference())
		// stub.setGatewayResponse(entity.getGatewayResponse())
		stub.setSessionId(entity.getSessionId())
		/*
		 * as no credit card information available for this replication we should not even try to set it stub.setCreditCardExpiry(entity.getCreditCardExpiry())
		 * stub.setCreditCardName(entity.getCreditCardName()) stub.setCreditCardNumber(entity.getCreditCardNumber()) if (entity.getCreditCardType() != null) {
		 * stub.setCreditCardType(entity.getCreditCardType().getDatabaseValue()) }
		 */
		stub.setSource(entity.getSource().getDatabaseValue())
		if (entity.getStatus() != null) {
			stub.setStatus(entity.getStatus().getDatabaseValue())
		}
		stub.setType(entity.getPaymentMethod().getType().getDatabaseValue())

		stub.setDateBanked(LocalDateUtils.valueToDate(entity.getDateBanked()))

		if (entity.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue())
		}
		stub.setBillingId(entity.getBillingId())

		return stub
	}
}
