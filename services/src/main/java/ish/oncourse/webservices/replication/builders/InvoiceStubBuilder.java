package ish.oncourse.webservices.replication.builders;

import ish.common.types.PaymentSource;
import ish.oncourse.model.Invoice;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceStub;

public class InvoiceStubBuilder extends AbstractWillowStubBuilder<Invoice, InvoiceStub> {

	@Override
	protected InvoiceStub createFullStub(Invoice entity) {

		InvoiceStub stub = new InvoiceStub();
		stub.setAmountOwing(entity.getAmountOwing());
		stub.setBillToAddress(entity.getBillToAddress());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setCustomerPO(entity.getCustomerPO());
		stub.setCustomerReference(entity.getCustomerReference());
		stub.setDateDue(entity.getDateDue());
		stub.setDescription(entity.getDescription());
		stub.setInvoiceDate(entity.getInvoiceDate());
		stub.setInvoiceNumber(entity.getInvoiceNumber());
		stub.setModified(entity.getModified());
		stub.setPublicNotes(entity.getPublicNotes());
		stub.setShippingAddress(entity.getShippingAddress());

		if (entity.getSource() != null) {
			stub.setSource(entity.getSource().getDatabaseValue());
		} else {
			stub.setSource(PaymentSource.SOURCE_WEB.getDatabaseValue());
		}

		stub.setStatus(entity.getStatus().name());
		stub.setTotalExGst(entity.getTotalExGst());
		stub.setTotalGst(entity.getTotalGst());
		return stub;
	}
}
