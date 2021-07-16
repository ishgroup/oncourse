/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.webservices.util.GenericReplicationStub
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
abstract class AbstractAngelUpdater<V extends GenericReplicationStub, T extends Queueable> implements IAngelUpdater {
	protected static final Logger LOG = LogManager.getLogger()
	static final String TUTOR_ENTITY_NAME = "Tutor"
	static final String STUDENT_ENTITY_NAME = "Student"
	static final String SITE_ENTITY_NAME = "Site"
	static final String ROOM_ENTITY_NAME = "Room"
	static final String REPORT_ENTITY_NAME = "Report"
	static final String COURSE_CLASS_ENTITY_NAME = "CourseClass"
	static final String COURSE_ENTITY_NAME = "Course"
	static final String CONTACT_ENTITY_NAME = "Contact"
	static final String DOCUMENT_ENTITY_NAME = "Document"
	static final String APPLICATION_ENTITY_NAME = "Application"
	static final String ENROLMENT_ENTITY_NAME = "Enrolment"
	static final String PAYSLIP_ENTITY_NAME = "Payslip"
	static final String WAITING_LIST_ENTITY_NAME = "WaitingList"
	static final String CERTIFICATE_ENTITY_NAME = "Certificate"
	static final String INVOICE_ENTITY_NAME = "Invoice"
	static final String PRIOR_LEARNING_ENTITY_NAME = "PriorLearning"
	static final String SESSION_ENTITY_NAME = "Session"
	static final String TAG_ENTITY_NAME = "Tag"
	static final String ASSESSMENT_ENTITY_NAME = "Assessment"
	static final String ASSESSMENT_SUBMISSION_ENTITY_NAME = "AssessmentSubmission"
	static final String SURVEY_ENTITY_NAME = "Survey"
	static final String ARTICLE_ENTITY_NAME = "Article"
	static final String MEMBERSHIP_ENTITY_NAME = "Membership"
	static final String VOUCHER_ENTITY_NAME = "Voucher"
	static final String LEAD_ENTITY_NAME = "Lead"
	/**
	 * @see IAngelUpdater#updateEntityFromStub(GenericReplicationStub,
	 *      Queueable, RelationShipCallback)
	 */
	void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		entity.setWillowId(stub.getWillowId())
		updateEntity((V) stub, (T) entity, callback)
	}

	protected abstract void updateEntity(V stub, T entity, RelationShipCallback callback)
}
