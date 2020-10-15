package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Article
import ish.oncourse.webservices.v21.stubs.replication.ArticleStub

/**
 */
class ArticleStubBuilder extends AbstractProductItemStubBuilder<Article, ArticleStub> {

	@Override
	protected ArticleStub createFullStub(Article entity) {
		def stub =  super.createFullStub(entity)
		stub.setContactId(entity.getContact().getId())
		return stub
	}

	@Override
	protected ArticleStub createStub() {
		return new ArticleStub()
	}
}
