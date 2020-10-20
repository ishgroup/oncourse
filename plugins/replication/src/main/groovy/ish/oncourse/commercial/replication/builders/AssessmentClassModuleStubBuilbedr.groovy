/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.webservices.v21.stubs.replication.AssessmentClassModuleStub

/**
 * Created by anarut on 12/1/16.
 */
class AssessmentClassModuleStubBuilbedr extends AbstractAngelStubBuilder<AssessmentClassModule, AssessmentClassModuleStub> {

    @Override
    protected AssessmentClassModuleStub createFullStub(AssessmentClassModule entity) {
        def stub = new AssessmentClassModuleStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setAssessmentClassId(entity.getAssessmentClass().getId())
        stub.setModuleId(entity.getModule().getWillowId())
        return stub
    }
}
