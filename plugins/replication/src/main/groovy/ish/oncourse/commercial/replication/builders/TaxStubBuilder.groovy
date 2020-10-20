/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Tax
import ish.oncourse.webservices.v21.stubs.replication.TaxStub

class TaxStubBuilder extends AbstractAngelStubBuilder<Tax, TaxStub>{
    @Override
    protected TaxStub createFullStub(Tax entity) {
        final def stub = new TaxStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setRate(entity.getRate())
        stub.setIsGSTTaxType(entity.getIsGSTTaxType())
        stub.setDescription(entity.getDescription())
        stub.setTaxCode(entity.getTaxCode())
        if (entity.getCreatedBy() != null) {
            stub.setCreatedBy(entity.getCreatedBy().getId())
        }
        return stub
    }
}
