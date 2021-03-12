/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.webservices.v23.stubs.replication.AssessmentSubmissionStub

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
        stub.setSubmittedById(entity.getSubmittedBy()?.getId())
        stub.setSubmittedOn(entity.getSubmittedOn())
        stub.setMarkedOn(entity.getMarkedOn())
        return stub
    }
}
