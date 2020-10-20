/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

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
