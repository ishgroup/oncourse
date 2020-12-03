/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.webservices.v23.stubs.replication.AssessmentSubmissionStub

/**
 * Created by Artem on 24/10/2016.
 */
class AssessmentSubmissionUpdater extends AbstractAngelUpdater<AssessmentSubmissionStub, AssessmentSubmission> {

    @Override
    protected void updateEntity(AssessmentSubmissionStub stub, AssessmentSubmission entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class))
        entity.setEnrolment(callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class))
        entity.setSubmittedBy(callback.updateRelationShip(stub.getSubmittedById(), Contact.class))
        entity.setTutorComments(stub.getTutorComments())
        entity.setStudentComments(stub.getStudentComments())
    }
}
