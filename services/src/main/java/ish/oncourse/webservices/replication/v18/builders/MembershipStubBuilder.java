package ish.oncourse.webservices.replication.v18.builders;

import ish.oncourse.model.Membership;
import ish.oncourse.webservices.v18.stubs.replication.MembershipStub;

public class MembershipStubBuilder extends AbstractProductItemStubBuilder<Membership, MembershipStub> {

	@Override
	protected MembershipStub createFullStub(Membership entity) {
		MembershipStub stub = super.createFullStub(entity);
		stub.setContactId(entity.getContact().getId());
		stub.setExpiryDate(entity.getExpiryDate());
		return stub;
	}
	
	@Override
	protected MembershipStub createStub() {
		return new MembershipStub();
	}

}
