/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.DocumentVersionStub;

public class DocumentVersionUpdater extends AbstractWillowUpdater<DocumentVersionStub, DocumentVersion> {
	
	@Override
	protected void updateEntity(DocumentVersionStub stub, DocumentVersion entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setFileName(stub.getFileName());
		entity.setByteSize(stub.getByteSize());
		entity.setMimeType(stub.getMimeType());
		entity.setPixelHeight(stub.getPixelHeight());
		entity.setPixelWidth(stub.getPixelWidth());
		entity.setVersionId(stub.getVersionId());
		entity.setTimestamp(stub.getTimestamp());
		entity.setThumbnail(stub.getThumbnail());
		
		entity.setDocument(callback.updateRelationShip(stub.getDocumentId(), Document.class));
		entity.setDescription(stub.getDescription());
		entity.setCreatedByUser(callback.updateRelationShip(stub.getCreatedByUserId(), SystemUser.class));
	}
}
