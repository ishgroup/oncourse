package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.webservices.v21.stubs.replication.AssessmentClassTutorStub

/**
 * Created by Artem on 24/10/2016.
 */
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
