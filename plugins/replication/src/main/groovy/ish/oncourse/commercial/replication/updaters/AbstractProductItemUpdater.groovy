/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.common.types.ConfirmationStatus
import ish.common.types.ProductStatus
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.webservices.v23.stubs.replication.ProductItemStub

@CompileStatic
abstract class AbstractProductItemUpdater<S extends ProductItemStub, E extends ProductItem> extends AbstractAngelUpdater<S, E> {

	protected void updateEntity(S stub, E entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setType(stub.getType())
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class))
		if (stub.getStatus() != null) {
			entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ProductStatus.class))
		} else {
			def message = String.format(
				"Unable to update product item with id=%s ,willow id=%s and type=%s because empty status passed!",
					stub.getAngelId(), stub.getWillowId(), stub.getType())
			LOG.debug(message, new Exception(message))
			throw new IllegalArgumentException(message, new Exception(message))
		}

		entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class))

	}
}
