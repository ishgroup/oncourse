package ish.oncourse.webservices.replication.v8.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.MembershipProductStub;

public class MembershipProductUpdater extends AbstractProductUpdater<MembershipProductStub, MembershipProduct> {

	@Override
	protected void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setExpiryDays(stub.getExpiryDays());
		if (stub.getExpiryType() != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class));
		}
	}
}
