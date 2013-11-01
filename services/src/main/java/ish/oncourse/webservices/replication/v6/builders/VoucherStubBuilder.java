package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Voucher;
import ish.oncourse.webservices.v6.stubs.replication.VoucherStub;

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
		stub.setSource(entity.getSource().getDatabaseValue());
		return stub;
	}
	
	protected VoucherStub createStub() {
		return new VoucherStub();
	}

}
