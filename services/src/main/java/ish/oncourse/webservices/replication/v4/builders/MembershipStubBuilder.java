package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.Membership;
import ish.oncourse.webservices.v4.stubs.replication.MembershipStub;

public class MembershipStubBuilder extends AbstractWillowStubBuilder<Membership, MembershipStub> {

	@Override
	protected MembershipStub createFullStub(Membership entity) {
		//do nothing because we need to cleanup the generated queued record but angel side not able to parse this stub
		return null;
	}

}
