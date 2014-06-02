package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.v7.stubs.replication.MembershipProductStub;

public class MembershipProductStubBuilder extends AbstractProductStubBuilder<MembershipProduct, MembershipProductStub> {

	@Override
	protected MembershipProductStub createFullStub(MembershipProduct entity) {
		MembershipProductStub stub = super.createFullStub(entity);
		stub.setExpiryDays(entity.getExpiryDays());
		stub.setExpiryType(entity.getExpiryType().getDatabaseValue());
		return stub;
	}

	@Override
	protected MembershipProductStub createStub() {
		return new MembershipProductStub();
	}
}
