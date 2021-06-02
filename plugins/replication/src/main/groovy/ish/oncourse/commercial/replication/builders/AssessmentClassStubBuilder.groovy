/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.webservices.v23.stubs.replication.AssessmentClassStub

@CompileStatic
class AssessmentClassStubBuilder extends AbstractAngelStubBuilder<AssessmentClass, AssessmentClassStub> {
    @Override
    protected AssessmentClassStub createFullStub(AssessmentClass entity) {
        def stub = new AssessmentClassStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setAssessmentId(entity.getAssessment().getId())
        stub.setCourseClassId(entity.getCourseClass().getId())
        stub.setDueDate(entity.getDueDate())
        stub.setReleaseDate(entity.getReleaseDate())
        return stub
    }
}
