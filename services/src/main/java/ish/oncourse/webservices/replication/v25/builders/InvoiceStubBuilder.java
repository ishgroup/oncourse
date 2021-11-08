package ish.oncourse.webservices.replication.v25.builders;

import ish.oncourse.model.AbstractInvoice;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v25.stubs.replication.InvoiceStub;

public class InvoiceStubBuilder extends AbstractWillowStubBuilder<AbstractInvoice, InvoiceStub> {

	@Override
	protected InvoiceStub createFullStub(AbstractInvoice entity) {
		InvoiceStub stub = new InvoiceStub();
		stub.setAmountOwing(entity.getAmountOwing().toBigDecimal());
		stub.setBillToAddress(entity.getBillToAddress());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setCustomerReference(entity.getCustomerReference());
		stub.setDateDue(entity.getDateDue());
		stub.setDescription(entity.getDescription());
		stub.setInvoiceDate(entity.getInvoiceDate());
		stub.setInvoiceNumber(entity.getInvoiceNumber());
		stub.setModified(entity.getModified());
		stub.setPublicNotes(entity.getPublicNotes());
		stub.setShippingAddress(entity.getShippingAddress());
		stub.setSource(entity.getSource().getDatabaseValue());
		stub.setTotalExGst(entity.getTotalExGst().toBigDecimal());
		stub.setTotalGst(entity.getTotalGst().toBigDecimal());
		if (entity.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue());
		}
		stub.setType(entity.getType().getDatabaseValue());
		stub.setAllowAutoPay(entity.getAllowAutoPay());
		stub.setTitle(entity.getTitle());
		if (entity instanceof Invoice) {
			CorporatePass corporatePass = ((Invoice) entity).getCorporatePassUsed();
			stub.setCorporatePassId(corporatePass != null ? corporatePass.getId() : null);

			PaymentIn authorisedRebillingCard = ((Invoice) entity).getAuthorisedRebillingCard();
			stub.setAuthorisedRebillingCardId(authorisedRebillingCard != null ? authorisedRebillingCard.getId() : null);
		}
		return stub;
	}
}
