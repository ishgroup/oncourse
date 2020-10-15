package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.webservices.v21.stubs.replication.AssessmentClassStub

/**
 * Created by Artem on 22/10/2016.
 */
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
