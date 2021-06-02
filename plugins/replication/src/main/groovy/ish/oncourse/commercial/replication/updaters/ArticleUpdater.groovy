/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.webservices.v23.stubs.replication.ArticleStub

@CompileStatic
class ArticleUpdater extends AbstractProductItemUpdater<ArticleStub, Article> {

	@Override
	protected void updateEntity(ArticleStub stub, Article entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback)

		entity.setProduct(callback.updateRelationShip(stub.getProductId(), ArticleProduct.class))
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class))
	}
}
