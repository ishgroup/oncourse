/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Product
import ish.oncourse.webservices.v23.stubs.replication.ProductStub

/**
 */
class ProductStubBuilder extends AbstractProductStubBuilder<Product, ProductStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected ProductStub createFullStub(final Product entity) {
		return super.createFullStub(entity) as ProductStub
	}

	@Override
	protected ProductStub createStub() {
		return new ProductStub()
	}
}
