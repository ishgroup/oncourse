package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.ArticleProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.ArticleProductStub;

public class ArticleProductUpdater extends AbstractProductUpdater<ArticleProductStub, ArticleProduct> {
	@Override
	protected void updateEntity(ArticleProductStub stub, ArticleProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
	}
}
