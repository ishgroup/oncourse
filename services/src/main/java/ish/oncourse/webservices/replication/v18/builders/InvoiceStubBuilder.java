package ish.oncourse.webservices.replication.v18.builders;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Invoice;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v18.stubs.replication.InvoiceStub;

public class InvoiceStubBuilder extends AbstractWillowStubBuilder<Invoice, InvoiceStub> {

	@Override
	protected InvoiceStub createFullStub(Invoice entity) {
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
		CorporatePass corporatePass = entity.getCorporatePassUsed();
		stub.setCorporatePassId(corporatePass != null ? entity.getCorporatePassUsed().getId() : null);
		if (entity.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue());
		}
		stub.setAuthorisedRebillingCardId(entity.getAuthorisedRebillingCard() != null ? entity.getAuthorisedRebillingCard().getId() : null);
		stub.setType(entity.getType().getDatabaseValue());
		return stub;
	}
}
