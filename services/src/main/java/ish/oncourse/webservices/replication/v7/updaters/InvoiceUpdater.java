package ish.oncourse.webservices.replication.v7.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Invoice;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.InvoiceStub;

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
	}

}
