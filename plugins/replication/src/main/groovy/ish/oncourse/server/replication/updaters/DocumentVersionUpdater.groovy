/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.webservices.v21.stubs.replication.DocumentVersionStub

class DocumentVersionUpdater extends AbstractAngelUpdater<DocumentVersionStub, DocumentVersion> {

	@Override
	protected void updateEntity(DocumentVersionStub stub, DocumentVersion entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		entity.setFileName(stub.getFileName())
		entity.setByteSize(stub.getByteSize())
		entity.setMimeType(stub.getMimeType())
		entity.setPixelHeight(stub.getPixelHeight())
		entity.setPixelWidth(stub.getPixelWidth())
		entity.setVersionId(stub.getVersionId())
		entity.setTimestamp(stub.getTimestamp())
		entity.setThumbnail(stub.getThumbnail())

		entity.setDocument(callback.updateRelationShip(stub.getDocumentId(), Document.class))
		entity.setDescription(stub.getDescription())
		entity.setCreatedByUser(callback.updateRelationShip(stub.getCreatedByUserId(), SystemUser.class))

	}
}
