/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.math.Money
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.Product
import ish.oncourse.webservices.v23.stubs.replication.ProductStub

abstract class AbstractProductUpdater<S extends ProductStub, E extends Product> extends AbstractAngelUpdater<S, E> {
    protected void updateEntity(S stub, E entity, RelationShipCallback callback) {
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
        if (stub.getFieldConfigurationSchemeId() != null) {
            entity.setFieldConfigurationScheme(callback.updateRelationShip(stub.getFieldConfigurationSchemeId(), FieldConfigurationScheme.class))
        }
    }
}
