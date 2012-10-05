package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.TypesUtil;
import ish.common.types.VoucherPaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.VoucherPaymentInStub;

public class VoucherPaymentInUpdater extends AbstractWillowUpdater<VoucherPaymentInStub, VoucherPaymentIn> {

	@Override
	protected void updateEntity(VoucherPaymentInStub stub, VoucherPaymentIn entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		if (stub.getEnrolmentsCount() == null) {
			stub.setEnrolmentsCount(0);
		}
		entity.setEnrolmentsCount(stub.getEnrolmentsCount());
		entity.setModified(stub.getModified());
		PaymentIn paymentIn = callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class);
		entity.setPayment(paymentIn);
		if (stub.getStatus() != null) {
			VoucherPaymentStatus status = TypesUtil.getEnumForDatabaseValue(stub.getStatus(), VoucherPaymentStatus.class);
			entity.setStatus(status);
		}
		Voucher voucher = callback.updateRelationShip(stub.getVoucherId(), Voucher.class);
		entity.setVoucher(voucher);
	}

}
