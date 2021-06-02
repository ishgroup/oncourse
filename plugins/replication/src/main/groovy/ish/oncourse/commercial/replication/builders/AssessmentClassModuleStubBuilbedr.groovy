/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.webservices.v23.stubs.replication.AssessmentClassModuleStub

@CompileStatic
class AssessmentClassModuleStubBuilbedr extends AbstractAngelStubBuilder<AssessmentClassModule, AssessmentClassModuleStub> {

    @Override
    protected AssessmentClassModuleStub createFullStub(AssessmentClassModule entity) {
        AssessmentClassModuleStub stub = new AssessmentClassModuleStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setAssessmentClassId((entity.getAssessmentClass() as ish.oncourse.server.cayenne.AssessmentClass).getId())
        stub.setModuleId((entity.getModule() as ish.oncourse.server.cayenne.Module).getWillowId() )
        return stub
    }
}
