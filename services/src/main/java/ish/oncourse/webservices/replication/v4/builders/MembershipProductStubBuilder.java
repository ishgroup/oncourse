package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.v4.stubs.replication.MembershipProductStub;

public class MembershipProductStubBuilder extends AbstractWillowStubBuilder<MembershipProduct, MembershipProductStub> {

	@Override
	protected MembershipProductStub createFullStub(MembershipProduct entity) {
		//do nothing because we need to cleanup the generated queued record but angel side not able to parse this stub
		return null;
	}

}
