package ish.oncourse.webservices.replication.v6.updaters;

import ish.common.types.TypesUtil;
import ish.common.types.VoucherPaymentStatus;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.VoucherPaymentInStub;

public class VoucherPaymentInUpdater extends AbstractWillowUpdater<VoucherPaymentInStub, VoucherPaymentIn> {

	@Override
	protected void updateEntity(VoucherPaymentInStub stub, VoucherPaymentIn entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		if (stub.getEnrolmentsCount() == null) {
			stub.setEnrolmentsCount(0);
		}
		entity.setEnrolmentsCount(stub.getEnrolmentsCount());
		entity.setModified(stub.getModified());
		entity.setPayment(callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class));
		if (stub.getStatus() != null) {
			entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), VoucherPaymentStatus.class));
		} else {
			// TODO: temporarily (while we are not redeeming vouchers on willow) this defaults to APPROVED since
			// there is no logic checking voucher for concurrent usage or consistency
			entity.setStatus(VoucherPaymentStatus.APPROVED);
		}
		entity.setVoucher(callback.updateRelationShip(stub.getVoucherId(), Voucher.class));
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class));
	}

}
