package ish.oncourse.webservices.replication.v22.updaters;

import ish.oncourse.model.ArticleProduct;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v22.stubs.replication.ArticleProductStub;

public class ArticleProductUpdater extends AbstractProductUpdater<ArticleProductStub, ArticleProduct> {
	@Override
	protected void updateEntity(ArticleProductStub stub, ArticleProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
	}
}
