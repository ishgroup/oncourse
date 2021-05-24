/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v23.stubs.replication.CustomFieldStub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFieldUpdater extends AbstractWillowUpdater<CustomFieldStub, CustomField> {

	private static final Logger logger = LogManager.getLogger();
	
	@Override
	protected void updateEntity(CustomFieldStub stub, CustomField entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setCustomFieldType(callback.updateRelationShip(stub.getCustomFieldTypeId(), CustomFieldType.class));
		entity.setValue(stub.getValue());
		entity.setEntityName(stub.getEntityName());
		
		IExpandable relatedObject;
		if (CONTACT_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Contact.class);
		} else if (ENROLMENT_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Enrolment.class);
		} else if (COURSE_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Course.class);
		} else if (APPLICATION_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Application.class);
		} else if (WAITING_LIST_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), WaitingList.class);
		} else if (SURVEY_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Survey.class);
		} else if (COURSE_CLASS_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), CourseClass.class);
		} else if (ARTICLE_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Article.class);
		} else if (MEMBERSHIP_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Membership.class);
		} else if (VOUCHER_ENTITY_NAME.equals(stub.getEntityName())) {
			relatedObject = callback.updateRelationShip(stub.getForeignId(), Voucher.class);
		} else {
			String message = String.format("Unexpected related entity with type %s and angelId %s",
					stub.getEntityName(), stub.getForeignId());
			logger.error(message);
			throw new UpdaterException(message);
		}

		entity.setRelatedObject(relatedObject);
	}
}
