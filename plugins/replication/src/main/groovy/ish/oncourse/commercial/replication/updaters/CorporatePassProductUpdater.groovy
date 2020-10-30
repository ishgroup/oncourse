/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassProduct
import ish.oncourse.server.cayenne.Product
import ish.oncourse.webservices.v22.stubs.replication.CorporatePassProductStub

class CorporatePassProductUpdater extends AbstractAngelUpdater<CorporatePassProductStub, CorporatePassProduct> {
	@Override
	protected void updateEntity(CorporatePassProductStub stub, CorporatePassProduct entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class))
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), Product.class))
	}
}
