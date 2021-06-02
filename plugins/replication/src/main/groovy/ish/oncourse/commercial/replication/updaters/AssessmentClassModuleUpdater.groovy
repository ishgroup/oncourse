/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.commercial.replication.reference.ReferenceUtil
import ish.oncourse.webservices.v23.stubs.replication.AssessmentClassModuleStub

@CompileStatic
class AssessmentClassModuleUpdater extends AbstractAngelUpdater<AssessmentClassModuleStub, AssessmentClassModule> {

    @Override
    protected void updateEntity(AssessmentClassModuleStub stub, AssessmentClassModule entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class))
        entity.setModule(ReferenceUtil.findModuleByWillowId(entity.getObjectContext(), stub.getModuleId()))
    }
}
