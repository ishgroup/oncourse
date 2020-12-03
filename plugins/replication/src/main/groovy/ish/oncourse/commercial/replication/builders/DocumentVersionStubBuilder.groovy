/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.webservices.v23.stubs.replication.DocumentVersionStub

class DocumentVersionStubBuilder extends AbstractAngelStubBuilder<DocumentVersion, DocumentVersionStub> {

	@Override
	protected DocumentVersionStub createFullStub(DocumentVersion entity) {
		def stub = new DocumentVersionStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())

		stub.setDocumentId(entity.getDocument().getId())

		stub.setFileName(entity.getFileName())
		stub.setVersionId(entity.getVersionId())
		stub.setByteSize(entity.getByteSize())
		stub.setMimeType(entity.getMimeType())
		stub.setPixelHeight(entity.getPixelHeight())
		stub.setPixelWidth(entity.getPixelWidth())
		stub.setTimestamp(entity.getTimestamp())
		stub.setThumbnail(entity.getThumbnail())
		stub.setDescription(entity.getDescription())
		if (entity.getCreatedByUser() != null) {
			stub.setCreatedByUserId(entity.getCreatedByUser().getId())
		}
		return stub
	}
}
