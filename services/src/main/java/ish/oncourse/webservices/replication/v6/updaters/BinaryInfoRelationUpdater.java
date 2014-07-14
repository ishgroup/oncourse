package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v6.stubs.replication.BinaryInfoRelationStub;

public class BinaryInfoRelationUpdater extends AbstractWillowUpdater<BinaryInfoRelationStub, BinaryInfoRelation> {

	@Override
	public void updateEntity(BinaryInfoRelationStub stub, BinaryInfoRelation entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setBinaryInfo(callback.updateRelationShip(stub.getBinaryInfoId(), BinaryInfo.class));
		
		// Document records in v6 replication should always have the same angel id as BinaryInfo records
		// so it is safe to fetch document using binaryInfoId field of the stub 
		entity.setDocument(callback.updateRelationShip(stub.getBinaryInfoId(), Document.class));
		
		entity.setDocumentVersion(callback.updateRelationShip(stub.getDocumentVersionId(), DocumentVersion.class));
		
		entity.setEntityAngelId(stub.getEntityAngelId());
		entity.setEntityIdentifier(stub.getEntityName());
		
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
			LOG.error(message);
			throw new UpdaterException(message);
		}
		if (entityObject != null && entityObject.getId() != null) {
			entity.setEntityWillowId(entityObject.getId());
		} else {
			String message = String.format(
				"Unable to load related entity %s for angelid %s or this entity have null willowId",
					stub.getEntityName(), stub.getEntityAngelId());
			LOG.error(message);
			throw new UpdaterException(message);
		}
	}
}
