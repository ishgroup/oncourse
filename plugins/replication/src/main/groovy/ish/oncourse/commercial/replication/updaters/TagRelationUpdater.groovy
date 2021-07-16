/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.v23.stubs.replication.TagRelationStub
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.SelectQuery

/**
 */
class TagRelationUpdater extends AbstractAngelUpdater<TagRelationStub, TagRelation> {
	static final String WILLOW_ID_COLUMN = "willowId"
	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(TagRelationStub stub, TagRelation entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		def entityName = stub.getEntityName()
		Class<?> entityClass = null
		if (DOCUMENT_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Document.class
			entity.setEntityIdentifier(TaggableClasses.DOCUMENT.getDatabaseValue())
		} else if (CONTACT_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Contact.class
			entity.setEntityIdentifier(TaggableClasses.CONTACT.getDatabaseValue())
		} else if (COURSE_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Course.class
			entity.setEntityIdentifier(TaggableClasses.COURSE.getDatabaseValue())
		} else if (COURSE_CLASS_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = CourseClass.class
			entity.setEntityIdentifier(TaggableClasses.COURSE_CLASS.getDatabaseValue())
		} else if (REPORT_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Report.class
			entity.setEntityIdentifier(TaggableClasses.REPORT.getDatabaseValue())
		} else if (ROOM_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Room.class
			entity.setEntityIdentifier(TaggableClasses.ROOM.getDatabaseValue())
		} else if (SITE_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Site.class
			entity.setEntityIdentifier(TaggableClasses.SITE.getDatabaseValue())
		} else if (STUDENT_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Student.class
			entity.setEntityIdentifier(TaggableClasses.STUDENT.getDatabaseValue())
		} else if (TUTOR_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Tutor.class
			entity.setEntityIdentifier(TaggableClasses.TUTOR.getDatabaseValue())
		} else if (APPLICATION_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Application.class
			entity.setEntityIdentifier(TaggableClasses.APPLICATION.getDatabaseValue())
		} else if (ENROLMENT_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Application.class
			entity.setEntityIdentifier(TaggableClasses.ENROLMENT.getDatabaseValue())
		} else if (WAITING_LIST_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = WaitingList.class
			entity.setEntityIdentifier(TaggableClasses.WAITING_LIST.getDatabaseValue())
		} else if (ASSESSMENT_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Assessment.class
			entity.setEntityIdentifier(TaggableClasses.ASSESSMENT.getDatabaseValue())
		} else if (LEAD_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityClass = Lead.class
			entity.setEntityIdentifier(TaggableClasses.LEAD.getDatabaseValue())
		}
		if (entityClass != null && entity.getEntityAngelId() == null) {
			def objectContext = entity.getObjectContext()
			def expr = ExpressionFactory.matchDbExp(WILLOW_ID_COLUMN, stub.getEntityWillowId())
			def q = SelectQuery.query(entityClass, expr)
			def object = (Queueable) objectContext.selectOne(q)
			if (object != null) {
				entity.setEntityAngelId(object.getId())
			}
		}
		entity.setTag(callback.updateRelationShip(stub.getTagId(), Tag.class))
	}
}
