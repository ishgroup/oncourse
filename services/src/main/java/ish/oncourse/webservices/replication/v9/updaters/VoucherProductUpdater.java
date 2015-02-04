package ish.oncourse.webservices.replication.v9.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.VoucherProductStub;

public class VoucherProductUpdater extends AbstractProductUpdater<VoucherProductStub, VoucherProduct> {

	@Override
	public void updateEntity(final VoucherProductStub stub, final VoucherProduct entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setExpiryDays(stub.getExpiryDays());
		if (stub.getExpiryType() != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class));
		}
		entity.setMaxCoursesRedemption(stub.getMaxCoursesRedemption());
		if (stub.getValue() != null) {
			entity.setValue(Money.valueOf(stub.getValue()));
		}
	}

}
