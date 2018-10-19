package ish.oncourse.webservices.replication.v18.builders;

import ish.oncourse.model.Article;
import ish.oncourse.webservices.v18.stubs.replication.ArticleStub;

public class ArticleStubBuilder extends AbstractProductItemStubBuilder<Article, ArticleStub> {

	@Override
	protected ArticleStub createFullStub(Article entity) {
		ArticleStub stub = super.createFullStub(entity);
		if (entity.getContact() != null) {
			stub.setContactId(entity.getContact().getId());
		}
		return stub;
	}

	@Override
	protected ArticleStub createStub() {
		return new ArticleStub();
	}
}
