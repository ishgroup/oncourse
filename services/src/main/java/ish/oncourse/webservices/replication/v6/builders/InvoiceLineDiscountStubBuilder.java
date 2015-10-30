package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Discount;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.InvoiceLineDiscountStub;

public class InvoiceLineDiscountStubBuilder extends AbstractWillowStubBuilder<InvoiceLineDiscount, InvoiceLineDiscountStub> {

	@Override
	protected InvoiceLineDiscountStub createFullStub(InvoiceLineDiscount entity) {
		InvoiceLineDiscountStub stub = new InvoiceLineDiscountStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		Discount discount = entity.getDiscount();
		if (discount != null) {
			stub.setDiscountId(discount.getId());
		}
		InvoiceLine invoiceLine = entity.getInvoiceLine();
		if (invoiceLine != null) {
			stub.setInvoiceLineId(invoiceLine.getId());
		}
		return stub;
	}

}
