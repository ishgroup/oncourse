/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.webservices.v23.stubs.replication.ArticleProductStub

@CompileStatic
class ArticleProductUpdater extends AbstractProductUpdater<ArticleProductStub, ArticleProduct> {
	@Override
	protected void updateEntity(ArticleProductStub stub, ArticleProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback)
	}
}
