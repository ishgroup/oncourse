package ish.oncourse.webservices.replication.v5.builders;

import ish.oncourse.model.Voucher;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v5.stubs.replication.VoucherStub;

public class VoucherStubBuilder extends AbstractWillowStubBuilder<Voucher, VoucherStub> {

	@Override
	protected VoucherStub createFullStub(final Voucher entity) {
		VoucherStub stub = new VoucherStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setCode(entity.getCode());
		stub.setContactId(entity.getContact().getId());
		stub.setExpiryDate(entity.getExpiryDate());
		stub.setInvoiceLineId(entity.getInvoiceLine().getId());
		stub.setKey(entity.getIdKey());
		stub.setProductId(entity.getProduct().getId());
		stub.setRedeemedCoursesCount(entity.getRedeemedCoursesCount());
		stub.setRedemptionValue(entity.getRedemptionValue().toBigDecimal());
		stub.setSource(entity.getSource().getDatabaseValue());
		stub.setStatus(entity.getStatus().getDatabaseValue());
		stub.setType(entity.getType());
		//TODO: add me in VoucherPaymentInStubBuilder 
		/*if (entity.getRedemptionPayment() != null) {
			stub.setPaymentInId(entity.getRedemptionPayment().getId());
		}*/
		return stub;
	}

}
