package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.VoucherProductStub;

public class VoucherProductUpdater extends AbstractProductUpdater<VoucherProductStub, VoucherProduct> {

	@Override
	protected void updateEntity(final VoucherProductStub stub, final VoucherProduct entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setExpiryDays(stub.getExpiryDays());
		Integer expiryType = stub.getExpiryType();
		if (expiryType != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(expiryType, ExpiryType.class));
		}
		entity.setMaxCoursesRedemption(stub.getMaxCoursesRedemption());
		if (stub.getValue() != null) {
			entity.setValue(Money.valueOf(stub.getValue()));
		}
	}

}
