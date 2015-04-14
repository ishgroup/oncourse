package ish.oncourse.webservices.replication.v8.updaters;

import ish.common.types.AttachmentSpecialType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v8.stubs.replication.BinaryInfoRelationStub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BinaryInfoRelationUpdater extends AbstractWillowUpdater<BinaryInfoRelationStub, BinaryInfoRelation> {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public void updateEntity(BinaryInfoRelationStub stub, BinaryInfoRelation entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setDocumentVersion(callback.updateRelationShip(stub.getDocumentVersionId(), DocumentVersion.class));
		entity.setDocument(callback.updateRelationShip(stub.getDocumentId(), Document.class));
		
		entity.setEntityAngelId(stub.getEntityAngelId());
		entity.setEntityIdentifier(stub.getEntityName());
		
		entity.setSpecialType(TypesUtil.getEnumForDatabaseValue(stub.getSpecialType(), AttachmentSpecialType.class));
		
		//after set the data, try to update relation related entity willowid
		Queueable entityObject;
		if (CONTACT_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Contact.class);
		} else if (COURSE_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Course.class);
		} else if (CERTIFICATE_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Certificate.class);
		} else if (COURSE_CLASS_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), CourseClass.class);
		} else if (ENROLMENT_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Enrolment.class);
		} else if (INVOICE_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Invoice.class);
		} else if (ROOM_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Room.class);
		} else if (SESSION_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Session.class);
		} else if (SITE_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Site.class);
		} else if (STUDENT_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Student.class);
		} else if (TUTOR_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Tutor.class);
		} else if (TAG_ENTITY_NAME.equalsIgnoreCase(stub.getEntityName())) {
			entityObject = callback.updateRelationShip(stub.getEntityAngelId(), Tag.class);
		} else {
			String message = String.format("Unexpected related entity with type %s and angelid %s",
				stub.getEntityName(), stub.getEntityAngelId());
			logger.error(message);
			throw new UpdaterException(message);
		}
		if (entityObject != null && entityObject.getId() != null) {
			entity.setEntityWillowId(entityObject.getId());
		} else {
			String message = String.format(
				"Unable to load related entity %s for angelid %s or this entity have null willowId",
					stub.getEntityName(), stub.getEntityAngelId());
			logger.error(message);
			throw new UpdaterException(message);
		}
	}
}
