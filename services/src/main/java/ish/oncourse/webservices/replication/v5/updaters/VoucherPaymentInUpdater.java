package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.VoucherPaymentInStub;

public class VoucherPaymentInUpdater extends AbstractWillowUpdater<VoucherPaymentInStub, VoucherPaymentIn> {

	@Override
	protected void updateEntity(final VoucherPaymentInStub stub, final VoucherPaymentIn entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		final PaymentIn paymentIn = callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class);
		entity.setPaymentIn(paymentIn);
		final Voucher voucher = callback.updateRelationShip(stub.getVoucherId(), Voucher.class);
		entity.setVoucher(voucher);
	}

}
