package ish.oncourse.webservices.replication.v4.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.v4.stubs.replication.MembershipProductStub;

public class MembershipProductUpdater extends AbstractWillowUpdater<MembershipProductStub, MembershipProduct> {

	@Override
	protected void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setExpiryDays(stub.getExpiryDays());
		final Integer expiryType = stub.getExpiryType();
		if (expiryType != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(expiryType, ExpiryType.class));
		}
		entity.setIsOnSale(stub.isIsOnSale());
		entity.setIsWebVisible(stub.isIsWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNotes(stub.getNotes());
		entity.setSku(stub.getSku());
		entity.setType(stub.getType());
		if (stub.getPriceExTax() != null) {
			entity.setPriceExTax(new Money(stub.getPriceExTax()));
		}
		if (stub.getTaxAdjustment() != null) {
			entity.setTaxAdjustment(new Money(stub.getTaxAdjustment()));
		}
		entity.setTaxAmount(Money.ZERO);
	}

}
