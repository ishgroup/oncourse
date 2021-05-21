/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Product
import ish.oncourse.webservices.v23.stubs.replication.ProductStub

/**
 */
class ProductStubBuilder extends AbstractAngelStubBuilder<Product, ProductStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected ProductStub createFullStub(final Product entity) {
		final def stub = new ProductStub()
		stub.setSku(entity.getSku())
		stub.setCreated(entity.getCreatedOn())
		stub.setDescription(entity.getDescription())
		stub.setAngelId(entity.getId())
		stub.setIsOnSale(entity.getIsOnSale())
		stub.setIsWebVisible(entity.getIsWebVisible())
		stub.setModified(entity.getModifiedOn())
		stub.setName(entity.getName())
		stub.setNotes(entity.getNotes())
		stub.setPriceExTax(entity.getPriceExTax().toBigDecimal())
		stub.setTaxAdjustment(entity.getTaxAdjustment().toBigDecimal())
		stub.setTaxAmount(entity.getFeeGST().toBigDecimal())
		stub.setType(entity.getType())
		stub.setWillowId(entity.getWillowId())
		if (entity.getFieldConfigurationScheme() != null) {
			stub.setFieldConfigurationSchemeId(entity.getFieldConfigurationScheme().getId())
		}
		return stub
	}
}
