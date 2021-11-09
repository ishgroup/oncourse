package ish.oncourse.webservices.replication.v25.updaters;

import ish.oncourse.model.ArticleProduct;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v25.stubs.replication.ArticleProductStub;

public class ArticleProductUpdater extends AbstractProductUpdater<ArticleProductStub, ArticleProduct> {
	@Override
	protected void updateEntity(ArticleProductStub stub, ArticleProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
	}
}
