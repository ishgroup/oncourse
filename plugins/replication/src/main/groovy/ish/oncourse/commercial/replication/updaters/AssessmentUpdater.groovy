/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.GradingType
import ish.oncourse.webservices.v23.stubs.replication.AssessmentStub

@CompileStatic
class AssessmentUpdater extends AbstractAngelUpdater<AssessmentStub, Assessment> {
    @Override
    protected void updateEntity(AssessmentStub stub, Assessment entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setActive(stub.isIsActive())
        entity.setCode(stub.getCode())
        entity.setName(stub.getName())
        entity.setDescription(stub.getDescription())
        entity.setGradingType(callback.updateRelationShip(stub.getGradingTypeId(), GradingType.class))
    }
}
