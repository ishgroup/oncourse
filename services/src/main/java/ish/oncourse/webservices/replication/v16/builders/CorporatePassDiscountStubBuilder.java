/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.CorporatePassDiscount;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.CorporatePassDiscountStub;

public class CorporatePassDiscountStubBuilder extends AbstractWillowStubBuilder<CorporatePassDiscount, CorporatePassDiscountStub> {
	@Override
	protected CorporatePassDiscountStub createFullStub(CorporatePassDiscount entity) {
		CorporatePassDiscountStub stub = new CorporatePassDiscountStub();
		stub.setCorporatePassId(entity.getCorporatePass().getId());
		stub.setDiscountId(entity.getDiscount().getId());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		return stub;
	}
}
