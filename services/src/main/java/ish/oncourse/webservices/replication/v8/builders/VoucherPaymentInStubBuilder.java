package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.VoucherPaymentInStub;

public class VoucherPaymentInStubBuilder extends AbstractWillowStubBuilder<VoucherPaymentIn, VoucherPaymentInStub>{

	@Override
	protected VoucherPaymentInStub createFullStub(VoucherPaymentIn entity) {
		VoucherPaymentInStub stub = new VoucherPaymentInStub();
		stub.setCreated(entity.getCreated());
		stub.setEnrolmentsCount(entity.getEnrolmentsCount());
		stub.setModified(entity.getModified());
		stub.setPaymentInId(entity.getPayment().getId());
		stub.setVoucherId(entity.getVoucher().getId());
		stub.setStatus(entity.getStatus().getDatabaseValue());
		if (entity.getInvoiceLine() != null) {
			stub.setInvoiceLineId(entity.getInvoiceLine().getId());
		}
		return stub;
	}

}
