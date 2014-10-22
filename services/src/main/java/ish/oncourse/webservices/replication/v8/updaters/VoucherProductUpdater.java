package ish.oncourse.webservices.replication.v8.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.VoucherProductStub;

public class VoucherProductUpdater extends AbstractProductUpdater<VoucherProductStub, VoucherProduct> {

	@Override
	protected void updateEntity(final VoucherProductStub stub, final VoucherProduct entity, final RelationShipCallback callback) {
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
