/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.webservices.v21.stubs.replication.ProductItemStub

/**
 */
class ProductItemStubBuilder extends AbstractProductItemStubBuilder<ProductItem, ProductItemStub> {

	@Override
	protected ProductItemStub createStub() {
		return new ProductItemStub()
	}
}
