/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.webservices.v23.stubs.replication.ArticleProductStub

@CompileStatic
class ArticleProductStubBuilder extends AbstractAngelStubBuilder<ArticleProduct, ArticleProductStub>{

	@Override
	protected ArticleProductStub createFullStub(ArticleProduct entity) {
		final def stub = new ArticleProductStub()
		stub.setSku(entity.getSku())
		stub.setCreated(entity.getCreatedOn())
		stub.setDescription(entity.getDescription())
		stub.setIsOnSale(entity.getIsOnSale())
		stub.setIsWebVisible(entity.getIsWebVisible())
		stub.setModified(entity.getModifiedOn())
		stub.setName(entity.getName())
		stub.setNotes(entity.getNotes())
		stub.setPriceExTax(entity.getPriceExTax().toBigDecimal())
		stub.setTaxAdjustment(entity.getTaxAdjustment().toBigDecimal())
		stub.setTaxAmount(entity.getFeeGST().toBigDecimal())
		stub.setType(entity.getType())
		stub.setWeight(entity.getWeight())
		return stub
	}
}
