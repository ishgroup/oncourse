/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.common.types.ExpiryType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.webservices.v21.stubs.replication.VoucherProductStub

class VoucherProductUpdater extends AbstractAngelUpdater<VoucherProductStub, VoucherProduct> {

	@Override
	protected void updateEntity(VoucherProductStub stub, VoucherProduct entity, RelationShipCallback callback) {
		entity.setSku(stub.getSku())
		entity.setCreatedOn(stub.getCreated())
		entity.setDescription(stub.getDescription())
		entity.setIsOnSale(stub.isIsOnSale())
		entity.setIsWebVisible(stub.isIsWebVisible())
		entity.setModifiedOn(stub.getModified())
		entity.setName(stub.getName())
		entity.setNotes(stub.getNotes())
		if (stub.getPriceExTax() != null ) {
			entity.setPriceExTax(new Money(stub.getPriceExTax()))
		}
		if (stub.getTaxAdjustment() != null ){
			entity.setTaxAdjustment(new Money(stub.getTaxAdjustment()))
		}
		entity.setType(stub.getType())
		entity.setExpiryDays(stub.getExpiryDays())
		if (stub.getExpiryType() != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class))
		}
		entity.setMaxCoursesRedemption(stub.getMaxCoursesRedemption())
		if (stub.getValue() != null) {
			entity.setValue(Money.valueOf(stub.getValue()))
		}
	}
}
