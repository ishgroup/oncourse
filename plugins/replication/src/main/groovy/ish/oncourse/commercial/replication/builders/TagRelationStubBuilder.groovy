/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.common.types.TypesUtil
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.commercial.replication.updaters.AbstractAngelUpdater
import ish.oncourse.server.cayenne.TagRelation
import ish.oncourse.webservices.v23.stubs.replication.TagRelationStub

import static ish.oncourse.cayenne.TaggableClasses.*

/**
 */
class TagRelationStubBuilder extends AbstractAngelStubBuilder<TagRelation, TagRelationStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected TagRelationStub createFullStub(TagRelation entity) {
		def stub = new TagRelationStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setTagId(entity.getTag().getId())
		stub.setEntityAngelId(entity.getEntityAngelId())
		TaggableClasses taggableClasses = TypesUtil.getEnumForDatabaseValue(entity.getEntityIdentifier(), TaggableClasses.class)
		switch (taggableClasses) {
			case DOCUMENT:
				stub.setEntityName(AbstractAngelUpdater.DOCUMENT_ENTITY_NAME)
				break
			case CONTACT:
				stub.setEntityName(AbstractAngelUpdater.CONTACT_ENTITY_NAME)
				break
			case COURSE:
				stub.setEntityName(AbstractAngelUpdater.COURSE_ENTITY_NAME)
				break
			case COURSE_CLASS:
				stub.setEntityName(AbstractAngelUpdater.COURSE_CLASS_ENTITY_NAME)
				break
			case REPORT:
				stub.setEntityName(AbstractAngelUpdater.REPORT_ENTITY_NAME)
				break
			case SITE:
				stub.setEntityName(AbstractAngelUpdater.SITE_ENTITY_NAME)
				break
			case STUDENT:
				stub.setEntityName(AbstractAngelUpdater.STUDENT_ENTITY_NAME)
				break
			case TUTOR:
				stub.setEntityName(AbstractAngelUpdater.TUTOR_ENTITY_NAME)
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
			case WAITING_LIST:
				stub.setEntityName(AbstractAngelUpdater.WAITING_LIST_ENTITY_NAME)
				break
			case ASSESSMENT:
				stub.setEntityName(AbstractAngelUpdater.ASSESSMENT_ENTITY_NAME)
				break
			case LEAD:
				stub.setEntityName(AbstractAngelUpdater.LEAD_ENTITY_NAME)
				break
			default:
				throw new IllegalStateException(taggableClasses.name() + "is not illegal for this operation")
		}
		return stub
	}
}
