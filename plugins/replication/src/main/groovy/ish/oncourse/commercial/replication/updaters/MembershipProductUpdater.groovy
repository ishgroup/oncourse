/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.ExpiryType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.webservices.v23.stubs.replication.MembershipProductStub

/**
 */
class MembershipProductUpdater extends AbstractAngelUpdater<MembershipProductStub, MembershipProduct> {

    @Override
    protected void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
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
        entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class))
        if (stub.getFieldConfigurationSchemeId() != null) {
            entity.setFieldConfigurationScheme(callback.updateRelationShip(stub.getFieldConfigurationSchemeId(), FieldConfigurationScheme.class))
        }
    }
}
