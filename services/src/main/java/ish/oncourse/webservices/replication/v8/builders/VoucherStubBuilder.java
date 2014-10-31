package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.Voucher;
import ish.oncourse.webservices.v8.stubs.replication.VoucherStub;

public class VoucherStubBuilder extends AbstractProductItemStubBuilder<Voucher, VoucherStub> {

	@Override
	protected VoucherStub createFullStub(final Voucher entity) {
		VoucherStub stub = super.createFullStub(entity);
		stub.setCode(entity.getCode());
		stub.setExpiryDate(entity.getExpiryDate());
		stub.setKey(entity.getIdKey());
		stub.setRedeemedCoursesCount(entity.getRedeemedCoursesCount());
		if (entity.getContact() != null) {
			stub.setContactId(entity.getContact().getId());
		}
		if (entity.getRedemptionValue() != null) {
			stub.setRedemptionValue(entity.getRedemptionValue().toBigDecimal());
		}
		if (entity.getValueOnPurchase() != null) {
			stub.setValueOnPurchase(entity.getValueOnPurchase().toBigDecimal());
		}
		stub.setSource(entity.getSource().getDatabaseValue());
		if (entity.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue());
		}
		return stub;
	}
	
	protected VoucherStub createStub() {
		return new VoucherStub();
	}

}
