/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.updaters.AbstractAngelUpdater
import ish.oncourse.server.cayenne.TagRequirement
import ish.oncourse.webservices.v23.stubs.replication.TagRequirementStub

import static ish.oncourse.cayenne.TaggableClasses.*

/**
 */
@CompileStatic
class TagRequirementStubBuilder extends AbstractAngelStubBuilder<TagRequirement, TagRequirementStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected TagRequirementStub createFullStub(TagRequirement entity) {
		def stub = new TagRequirementStub()
		stub.setCreated(entity.getCreatedOn())
		switch (entity.getEntityIdentifier()) {
			case COURSE:
				stub.setEntityName(AbstractAngelUpdater.COURSE_ENTITY_NAME)
				break
			case COURSE_CLASS:
				stub.setEntityName(AbstractAngelUpdater.COURSE_CLASS_ENTITY_NAME)
				break
			case STUDENT:
				stub.setEntityName(AbstractAngelUpdater.STUDENT_ENTITY_NAME)
				break
			case TUTOR:
				stub.setEntityName(AbstractAngelUpdater.TUTOR_ENTITY_NAME)
				break
			case REPORT:
				stub.setEntityName(AbstractAngelUpdater.REPORT_ENTITY_NAME)
				break
			case DOCUMENT:
				stub.setEntityName(AbstractAngelUpdater.DOCUMENT_ENTITY_NAME)
				break
			case CONTACT:
				stub.setEntityName(AbstractAngelUpdater.CONTACT_ENTITY_NAME)
				break
			case SITE:
				stub.setEntityName(AbstractAngelUpdater.SITE_ENTITY_NAME)
				break
			case ROOM:
				stub.setEntityName(AbstractAngelUpdater.ROOM_ENTITY_NAME)
				break
			case APPLICATION:
				stub.setEntityName(AbstractAngelUpdater.APPLICATION_ENTITY_NAME)
				break
			case ENROLMENT:
				stub.setEntityName(AbstractAngelUpdater.ENROLMENT_ENTITY_NAME)
				break
			case PAYSLIP:
				stub.setEntityName(AbstractAngelUpdater.PAYSLIP_ENTITY_NAME)
				break
			case WAITING_LIST:
				stub.setEntityName(AbstractAngelUpdater.WAITING_LIST_ENTITY_NAME)
				break
			case ASSESSMENT:
				stub.setEntityName(AbstractAngelUpdater.ASSESSMENT_ENTITY_NAME)
				break
			case LEAD:
				stub.setEntityName(AbstractAngelUpdater.LEAD_ENTITY_NAME)
				break
		}
		stub.setManyTermsAllowed(entity.getManyTermsAllowed())
		stub.setModified(entity.getModifiedOn())
		stub.setRequired(entity.getIsRequired())
		stub.setTagId(entity.getTag().getId())
		return stub
	}
}
