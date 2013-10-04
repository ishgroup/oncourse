package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.MembershipProductStub;

public class MembershipProductUpdater extends AbstractProductUpdater<MembershipProductStub, MembershipProduct> {

	@Override
	protected void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setExpiryDays(stub.getExpiryDays());
		final Integer expiryType = stub.getExpiryType();
		if (expiryType != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(expiryType, ExpiryType.class));
		}
	}
}
