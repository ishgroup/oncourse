/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.updaters;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v16.stubs.replication.CorporatePassProductStub;

import java.util.Arrays;

public class CorporatePassProductUpdater extends AbstractWillowUpdater<CorporatePassProductStub, CorporatePassProduct> {

	@Override
	protected void updateEntity(CorporatePassProductStub stub, CorporatePassProduct entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class));
		entity.setProduct(getProduct(stub.getProductId(), callback));
	}
	
	private Product getProduct(Long angelId, RelationShipCallback callback) {
		
		for (Class aClass : Arrays.asList(Product.class, VoucherProduct.class, ArticleProduct.class, MembershipProduct.class)) {
			try {
				return (Product) callback.updateRelationShip(angelId, aClass);
			} catch (IllegalArgumentException e) {
				continue;
			}
		}
		String message = String.format("Uncommitted object with angelId:%s and entityName: Product wasn't found.", angelId);
		throw new IllegalArgumentException(message);
	}
}
