/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.CorporatePassProduct;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.CorporatePassProductStub;

public class CorporatePassProductStubBuilder extends AbstractWillowStubBuilder<CorporatePassProduct, CorporatePassProductStub> {
	@Override
	protected CorporatePassProductStub createFullStub(CorporatePassProduct entity) {
		CorporatePassProductStub stub = new CorporatePassProductStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setCorporatePassId(entity.getCorporatePass().getId());
		stub.setProductId(entity.getProduct().getId());
		return stub;
	}
}
