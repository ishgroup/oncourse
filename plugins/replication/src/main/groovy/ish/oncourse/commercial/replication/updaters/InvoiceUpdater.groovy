/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.ConfirmationStatus
import ish.common.types.InvoiceType
import ish.common.types.PaymentSource
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.AbstractInvoice
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.webservices.v23.stubs.replication.InvoiceStub
import ish.util.LocalDateUtils

import java.time.ZoneId
import java.util.Objects

/**
 */
class InvoiceUpdater extends AbstractAngelUpdater<InvoiceStub, AbstractInvoice> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(InvoiceStub stub, AbstractInvoice entity, RelationShipCallback callback) {
		entity.setAmountOwing(new Money(stub.getAmountOwing()))
		entity.setBillToAddress(stub.getBillToAddress())
		def contact = callback.updateRelationShip(stub.getContactId(), Contact.class)
		entity.setContact(contact)
		entity.setCustomerReference(stub.getCustomerReference())
		entity.setDateDue(LocalDateUtils.dateToValue(stub.getDateDue()))
		entity.setCreatedOn(stub.getCreated())
		entity.setDescription(stub.getDescription())
		entity.setInvoiceDate(stub.getInvoiceDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
		if (stub.getInvoiceNumber() != null) {
			entity.setInvoiceNumber(stub.getInvoiceNumber())
		}
		final def source = TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class)
        entity.setSource(Objects.requireNonNullElse(source, PaymentSource.SOURCE_WEB))
		entity.setPublicNotes(stub.getPublicNotes())
		entity.setModifiedOn(stub.getModified())
		entity.setShippingAddress(stub.getShippingAddress())
		entity.setCorporatePassUsed(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class))
		if (stub.getConfirmationStatus() != null) {
			entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class))
		}
		if (stub.getAuthorisedRebillingCardId() != null) {
			entity.setAuthorisedRebillingCard(callback.updateRelationShip(stub.getAuthorisedRebillingCardId(), PaymentIn.class))
		}

		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), InvoiceType.class))
		entity.setAllowAutoPay(stub.isAllowAutoPay())
		entity.setTitle(stub.getTitle())
	}
}
