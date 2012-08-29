package ish.oncourse.webservices.replication.v5.builders;

import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v5.stubs.replication.VoucherPaymentInStub;

public class VoucherPaymentInStubBuilder extends AbstractWillowStubBuilder<VoucherPaymentIn, VoucherPaymentInStub> {

	@Override
	protected VoucherPaymentInStub createFullStub(final VoucherPaymentIn entity) {
		VoucherPaymentInStub stub = new VoucherPaymentInStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setPaymentInId(entity.getPaymentIn().getId());
		stub.setVoucherId(entity.getVoucher().getId());
		return stub;
	}

}
