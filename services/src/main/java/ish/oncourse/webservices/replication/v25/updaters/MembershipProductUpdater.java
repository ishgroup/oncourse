package ish.oncourse.webservices.replication.v25.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v25.stubs.replication.MembershipProductStub;

public class MembershipProductUpdater extends AbstractProductUpdater<MembershipProductStub, MembershipProduct> {

	@Override
	public void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setExpiryDays(stub.getExpiryDays());
		if (stub.getExpiryType() != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class));
		}
	}
}
