/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v13.updaters;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CorporatePassDiscount;
import ish.oncourse.model.Discount;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v13.stubs.replication.CorporatePassDiscountStub;

public class CorporatePassDiscountUpdater extends AbstractWillowUpdater<CorporatePassDiscountStub, CorporatePassDiscount> {
	@Override
	protected void updateEntity(CorporatePassDiscountStub stub, CorporatePassDiscount entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class));
	}
}
