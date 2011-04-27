package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.InvoiceLine;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub;

public class InvoiceLineStubBuilder extends AbstractWillowStubBuilder<InvoiceLine, InvoiceLineStub> {

	@Override
	protected InvoiceLineStub createFullStub(InvoiceLine entity) {
		InvoiceLineStub stub = new InvoiceLineStub();
		stub.setCreated(entity.getCreated());
		stub.setDescription(entity.getDescription());
		stub.setDiscountEachExTax(entity.getDiscountEachExTax().toBigDecimal());
		stub.setEnrolmentId(entity.getEnrolment().getId());
		stub.setEntityIdentifier(entity.getObjectId().getEntityName());
		stub.setInvoiceId(entity.getInvoice().getId());
		stub.setModified(entity.getModified());
		stub.setPriceEachExTax(entity.getPriceEachExTax().toBigDecimal());
		stub.setQuantity(entity.getQuantity());
		stub.setTaxEach(entity.getTaxEach().toBigDecimal());
		stub.setTitle(entity.getTitle());
		stub.setUnit(entity.getUnit());
		return stub;
	}
}
