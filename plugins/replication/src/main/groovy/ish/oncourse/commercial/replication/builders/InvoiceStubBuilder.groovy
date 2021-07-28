/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.AbstractInvoice
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.webservices.v23.stubs.replication.InvoiceStub
import ish.util.LocalDateUtils

import java.util.Date

/**
 */
class InvoiceStubBuilder extends AbstractAngelStubBuilder<AbstractInvoice, InvoiceStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected InvoiceStub createFullStub(AbstractInvoice inv) {
		def stub = new InvoiceStub()
		stub.setAmountOwing(inv.getAmountOwing().toBigDecimal())
		stub.setBillToAddress(inv.getBillToAddress())
		stub.setContactId(inv.getContact().getId())
		stub.setCreated(inv.getCreatedOn())
		stub.setCustomerReference(inv.getCustomerReference())
		stub.setDateDue(LocalDateUtils.valueToDateAtNoon(inv.getDateDue()))
		stub.setDescription(inv.getDescription())
		def invoiceDate = LocalDateUtils.valueToDateAtNoon(inv.getInvoiceDate())
		stub.setInvoiceDate(invoiceDate)
		stub.setInvoiceNumber(inv.getInvoiceNumber())
		stub.setModified(inv.getModifiedOn())
		stub.setPublicNotes(inv.getPublicNotes())
		stub.setShippingAddress(inv.getShippingAddress())
		stub.setSource(inv.getSource().getDatabaseValue())
		stub.setTotalExGst(inv.getTotal().toBigDecimal())
		stub.setTotalGst(inv.getTotalIncTax().toBigDecimal())

		def corporatePass = inv.getCorporatePassUsed()
		stub.setCorporatePassId(corporatePass != null ? inv.getCorporatePassUsed().getId() : null)

		if (inv.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(inv.getConfirmationStatus().getDatabaseValue())
		}
		stub.setAuthorisedRebillingCardId(inv.getAuthorisedRebillingCard() != null ? inv.getAuthorisedRebillingCard().getId() : null)
		stub.setType(inv.getType())
		stub.setAllowAutoPay(inv.getAllowAutoPay())
		return stub
	}
}
