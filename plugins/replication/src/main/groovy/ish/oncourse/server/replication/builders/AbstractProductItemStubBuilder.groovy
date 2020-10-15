/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.webservices.v21.stubs.replication.ProductItemStub

/**
 */
abstract class AbstractProductItemStubBuilder<E extends ProductItem, S extends ProductItemStub> extends AbstractAngelStubBuilder<E, S> {

	protected S createFullStub(final E entity) {
		final def stub = createStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setAngelId(entity.getId())
		stub.setModified(entity.getModifiedOn())
		stub.setType(entity.getType())
		stub.setInvoiceLineId(entity.getInvoiceLine().getId())
		stub.setProductId(entity.getProduct().getId())
		stub.setStatus(entity.getStatus().getDatabaseValue())
		stub.setWillowId(entity.getWillowId())
		stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue())
		
		return stub
	}

	protected abstract S createStub()

}
