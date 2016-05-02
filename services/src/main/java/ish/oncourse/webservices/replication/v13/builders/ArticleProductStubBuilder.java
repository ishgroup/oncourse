package ish.oncourse.webservices.replication.v13.builders;

import ish.oncourse.model.ArticleProduct;
import ish.oncourse.webservices.v13.stubs.replication.ArticleProductStub;

public class ArticleProductStubBuilder extends AbstractProductStubBuilder<ArticleProduct, ArticleProductStub> {
	@Override
	protected ArticleProductStub createFullStub(ArticleProduct entity) {
		ArticleProductStub stub = super.createFullStub(entity);
		return stub;
	}

	@Override
	protected ArticleProductStub createStub() {
		return new ArticleProductStub();
	}
}
