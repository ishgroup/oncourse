package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.webservices.v21.stubs.replication.AssessmentSubmissionStub

/**
 * Created by Artem on 24/10/2016.
 */
class AssessmentSubmissionStubBuilder extends AbstractAngelStubBuilder<AssessmentSubmission, AssessmentSubmissionStub> {

    @Override
    protected AssessmentSubmissionStub createFullStub(AssessmentSubmission entity) {
        def stub = new AssessmentSubmissionStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setEnrolmentId(entity.getEnrolment().getId())
        stub.setAssessmentClassId(entity.getAssessmentClass().getId())
        stub.setSubmittedById(entity.getSubmittedBy().getId())
        stub.setTutorComments(entity.getTutorComments())
        stub.setStudentComments(entity.getStudentComments())
        return stub
    }
}
