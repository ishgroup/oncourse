/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.webservices.v21.stubs.replication.VoucherProductStub

/**
 */
class VoucherProductStubBuilder extends AbstractAngelStubBuilder<VoucherProduct, VoucherProductStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
    @Override
    protected VoucherProductStub createFullStub(final VoucherProduct entity) {
        final def stub = new VoucherProductStub()
        stub.setSku(entity.getSku())
        stub.setCreated(entity.getCreatedOn())
        stub.setDescription(entity.getDescription())
        stub.setAngelId(entity.getId())
        stub.setIsOnSale(entity.getIsOnSale())
        stub.setIsWebVisible(entity.getIsWebVisible())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getName())
        stub.setNotes(entity.getNotes())
        if (entity.getPriceExTax() != null)
            stub.setPriceExTax(entity.getPriceExTax().toBigDecimal())
        if (entity.getTaxAdjustment() != null)
            stub.setTaxAdjustment(entity.getTaxAdjustment().toBigDecimal())
		stub.setTaxAmount(entity.getFeeGST().toBigDecimal())

        stub.setType(entity.getType())
        stub.setExpiryDays(entity.getExpiryDays())
        stub.setExpiryType(entity.getExpiryType().getDatabaseValue())
        if (entity.getValue() != null) {
            stub.setValue(entity.getValue().toBigDecimal())
        }
        stub.setWillowId(entity.getWillowId())
        stub.setMaxCoursesRedemption(entity.getMaxCoursesRedemption())
        return stub
    }

}
