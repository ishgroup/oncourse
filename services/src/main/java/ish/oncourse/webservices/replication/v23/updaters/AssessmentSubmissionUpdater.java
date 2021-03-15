/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.AssessmentClass;
import ish.oncourse.model.AssessmentSubmission;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.AssessmentSubmissionStub;

public class AssessmentSubmissionUpdater extends AbstractWillowUpdater<AssessmentSubmissionStub, AssessmentSubmission> {
	@Override
	protected void updateEntity(AssessmentSubmissionStub stub, AssessmentSubmission entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class));
		entity.setEnrolment(callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class));
		entity.setMarkedBy(callback.updateRelationShip(stub.getMarkedById(), Contact.class));
		entity.setSubmittedOn(stub.getSubmittedOn());
		entity.setMarkedOn(stub.getMarkedOn());
	}
}
