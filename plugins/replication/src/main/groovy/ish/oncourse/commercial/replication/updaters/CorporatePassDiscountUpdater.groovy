/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassDiscount
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.webservices.v21.stubs.replication.CorporatePassDiscountStub

class CorporatePassDiscountUpdater extends AbstractAngelUpdater<CorporatePassDiscountStub, CorporatePassDiscount> {

	@Override
	protected void updateEntity(CorporatePassDiscountStub stub, CorporatePassDiscount entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class))
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class))
	}
}
