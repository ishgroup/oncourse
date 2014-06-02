package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Article;
import ish.oncourse.model.ArticleProduct;
import ish.oncourse.model.Contact;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.ArticleStub;

public class ArticleUpdater extends AbstractProductItemUpdater<ArticleStub, Article> {
	@Override
	protected void updateEntity(ArticleStub stub, Article entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), ArticleProduct.class));
		if (stub.getContactId() != null) {
			entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		}
	}
}
