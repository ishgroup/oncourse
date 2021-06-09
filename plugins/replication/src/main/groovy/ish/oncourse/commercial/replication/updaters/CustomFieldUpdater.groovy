/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.v23.stubs.replication.CustomFieldStub
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CustomFieldUpdater extends AbstractAngelUpdater<CustomFieldStub, CustomField> {

	private static final Logger logger = LogManager.getLogger()

	@Override
	protected void updateEntity(CustomFieldStub stub, CustomField entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		entity.setCustomFieldType(callback.updateRelationShip(stub.getCustomFieldTypeId(), CustomFieldType.class))

		entity.setValue(stub.getValue())
		entity.setEntityIdentifier(stub.getEntityName())

		ExpandableTrait relatedObject
		switch (stub.getEntityName()) {
			case CONTACT_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Contact.class)
				break
			case COURSE_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Course.class)
				break
			case ENROLMENT_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Enrolment.class)
				break
			case APPLICATION_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Application.class)
				break
			case WAITING_LIST_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), WaitingList.class)
				break
			case SURVEY_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Survey.class)
				break
			case COURSE_CLASS_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), CourseClass.class)
				break
			case ARTICLE_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Article.class)
				break
			case MEMBERSHIP_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Membership.class)
				break
			case VOUCHER_ENTITY_NAME:
				relatedObject = callback.updateRelationShip(stub.getForeignId(), Voucher.class)
				break
			default:
				def message = String.format("Unexpected related entity with type %s and willowId %s",
						stub.getEntityName(), stub.getForeignId())
				logger.error(message)
				throw new IllegalArgumentException(message)
		}

		entity.setRelatedObject(relatedObject)
	}
}
