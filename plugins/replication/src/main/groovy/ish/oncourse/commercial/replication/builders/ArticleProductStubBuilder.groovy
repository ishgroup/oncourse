/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.webservices.v23.stubs.replication.ArticleProductStub

@CompileStatic
class ArticleProductStubBuilder extends AbstractProductStubBuilder<ArticleProduct, ArticleProductStub>{

	@Override
	protected ArticleProductStub createFullStub(ArticleProduct entity) {
		return super.createFullStub(entity) as ArticleProductStub
	}

	@Override
	protected ArticleProductStub createStub() {
		return new ArticleProductStub()
	}
}
