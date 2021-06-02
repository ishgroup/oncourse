/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.common.types.AttachmentSpecialType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.v23.stubs.replication.BinaryInfoRelationStub
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class AttachmentRelationUpdater extends AbstractAngelUpdater<BinaryInfoRelationStub, AttachmentRelation> {

    private static final Logger logger = LogManager.getLogger()

    @Override
    protected void updateEntity(BinaryInfoRelationStub stub, AttachmentRelation entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setDocument(callback.updateRelationShip(stub.getDocumentId(), Document.class))
        entity.setDocumentVersion(callback.updateRelationShip(stub.getDocumentId(), DocumentVersion.class))
        entity.setSpecialType(TypesUtil.getEnumForDatabaseValue(stub.getSpecialType(), AttachmentSpecialType.class))
        AttachableTrait attachable
        switch (stub.getEntityName()) {
            case (CONTACT_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Contact.class)
                break
            case (CERTIFICATE_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Certificate.class)
                break
            case (COURSE_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Course.class)
                break
            case (COURSE_CLASS_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), CourseClass.class)
                break
            case (ENROLMENT_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Enrolment.class)
                break
            case (INVOICE_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Invoice.class)
                break
            case (PRIOR_LEARNING_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), PriorLearning.class)
                break
            case (ROOM_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Room.class)
                break
            case (SITE_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Site.class)
                break
            case (SESSION_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Session.class)
                break
            case (STUDENT_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Student.class)
                break
            case (TAG_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Tag.class)
                break
            case (TUTOR_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Tutor.class)
                break
            case (APPLICATION_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Application.class)
                break
            case (ASSESSMENT_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), Assessment.class)
                break
            case (ASSESSMENT_SUBMISSION_ENTITY_NAME):
                attachable = callback.updateRelationShip(stub.getEntityWillowId(), AssessmentSubmission.class)
                break
            default:
                def message = String.format("Unexpected related entity with type %s and willowId %s",
                        stub.getEntityName(), stub.getEntityWillowId())
                logger.error(message)
                throw new IllegalArgumentException(message)
        }
        entity.setAttachedRelation(attachable)
    }
}
