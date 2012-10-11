package ish.oncourse.webservices.replication.v5.builders;

import ish.oncourse.model.Membership;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v5.stubs.replication.MembershipStub;

public class MembershipStubBuilder extends AbstractWillowStubBuilder<Membership, MembershipStub> {

	@Override
	protected MembershipStub createFullStub(Membership entity) {
		MembershipStub stub = new MembershipStub();
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setExpiryDate(entity.getExpiryDate());
		stub.setInvoiceLineId(entity.getInvoiceLine().getId());
		stub.setModified(entity.getModified());
		stub.setProductId(entity.getProduct().getId());
		stub.setType(entity.getType());
		return stub;
	}

}
