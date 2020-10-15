package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.webservices.v21.stubs.replication.ArticleStub

/**
 */
class ArticleUpdater extends AbstractProductItemUpdater<ArticleStub, Article> {

	@Override
	protected void updateEntity(ArticleStub stub, Article entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback)
		
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), ArticleProduct.class))
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class))
	}
}
