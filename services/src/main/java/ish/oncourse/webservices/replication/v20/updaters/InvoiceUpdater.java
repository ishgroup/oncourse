package ish.oncourse.webservices.replication.v20.updaters;

import ish.common.types.ConfirmationStatus;
import ish.common.types.InvoiceType;
import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v20.stubs.replication.InvoiceStub;

public class InvoiceUpdater extends AbstractWillowUpdater<InvoiceStub, Invoice> {

	@Override
	protected void updateEntity(InvoiceStub stub, Invoice entity, RelationShipCallback callback) {
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setAmountOwing(new Money(stub.getAmountOwing()));
		entity.setBillToAddress(stub.getBillToAddress());
		entity.setCreated(stub.getCreated());
		entity.setCustomerReference(stub.getCustomerReference());
		entity.setDateDue(stub.getDateDue());
		entity.setDescription(stub.getDescription());
		entity.setInvoiceDate(stub.getInvoiceDate());
		entity.setInvoiceNumber(stub.getInvoiceNumber());
		entity.setModified(stub.getModified());
		entity.setPublicNotes(stub.getPublicNotes());
		entity.setShippingAddress(stub.getShippingAddress());
		entity.setTotalExGst(new Money(stub.getTotalExGst()));
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class));
		entity.setTotalGst(new Money(stub.getTotalGst()));
		entity.setCorporatePassUsed(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class));
		if (stub.getConfirmationStatus() != null) {
			entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class));
		}
		if (stub.getAuthorisedRebillingCardId() != null) {
			entity.setAuthorisedRebillingCard(callback.updateRelationShip(stub.getAuthorisedRebillingCardId(), PaymentIn.class));
		}
		if (stub.getType() != null) {
			entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), InvoiceType.class));
		}
	}

}
