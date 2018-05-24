/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.updaters;

import ish.oncourse.model.Tax;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.TaxStub;

import java.util.Date;

public class TaxUpdater extends AbstractWillowUpdater<TaxStub, Tax> {
    
    @Override
    public void updateEntity(TaxStub stub, Tax entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated() != null ? stub.getCreated() : new Date());
        entity.setModified(stub.getModified() != null ? stub.getModified() : new Date());
        entity.setCode(stub.getTaxCode());
        entity.setDescription(stub.getDescription());
        entity.setIsGSTTaxType(stub.isIsGSTTaxType());
        entity.setRate(stub.getRate());
    }
}
