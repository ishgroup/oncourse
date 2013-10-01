package ish.oncourse.webservices.replication.v5.builders;

import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v5.stubs.replication.MembershipProductStub;

public class MembershipProductStubBuilder extends AbstractWillowStubBuilder<MembershipProduct, MembershipProductStub> {

	@Override
	protected MembershipProductStub createFullStub(MembershipProduct entity) {
		MembershipProductStub stub = new MembershipProductStub();
		stub.setCreated(entity.getCreated());
		stub.setDescription(entity.getDescription());
		stub.setExpiryDays(entity.getExpiryDays());
		stub.setExpiryType(entity.getExpiryType().getDatabaseValue());
		stub.setIsOnSale(entity.getIsOnSale());
		stub.setIsWebVisible(entity.getIsWebVisible());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setNotes(entity.getNotes());
		if (entity.getPriceExTax() != null) {
			stub.setPriceExTax(entity.getPriceExTax().toBigDecimal());
		}
		stub.setSku(entity.getSku());
		if (entity.getTaxAdjustment() != null) {
			stub.setTaxAdjustment(entity.getTaxAdjustment().toBigDecimal());
		}
		stub.setType(entity.getType());
		stub.setFeeGST(entity.getFeeGST().toBigDecimal());
		return stub;
	}

}
