/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.webservices.v23.stubs.replication.MembershipProductStub

/**
 */
class MembershipProductStubBuilder extends AbstractAngelStubBuilder<MembershipProduct, MembershipProductStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected MembershipProductStub createFullStub(final MembershipProduct entity) {
		final def stub = new MembershipProductStub()
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
		stub.setExpiryDays(entity.getExpiryDays())
		stub.setExpiryType(entity.getExpiryType().getDatabaseValue())
		return stub
	}
}
