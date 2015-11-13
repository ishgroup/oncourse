/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v11.updaters;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CorporatePassProduct;
import ish.oncourse.model.Product;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v11.stubs.replication.CorporatePassProductStub;

public class CorporatePassProductUpdater extends AbstractWillowUpdater<CorporatePassProductStub, CorporatePassProduct> {

	@Override
	protected void updateEntity(CorporatePassProductStub stub, CorporatePassProduct entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class));
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), Product.class));
	}
}
