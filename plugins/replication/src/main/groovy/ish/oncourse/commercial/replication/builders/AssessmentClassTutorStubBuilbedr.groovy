/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.webservices.v23.stubs.replication.AssessmentClassTutorStub

@CompileStatic
class AssessmentClassTutorStubBuilbedr extends AbstractAngelStubBuilder<AssessmentClassTutor, AssessmentClassTutorStub> {
    @Override
    protected AssessmentClassTutorStub createFullStub(AssessmentClassTutor entity) {
        def stub = new AssessmentClassTutorStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setAssessmentClassId(entity.getAssessmentClass().getId())
        stub.setTutorId(entity.getTutor().getId())
        return stub
    }
}
