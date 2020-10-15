/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Document
import ish.oncourse.webservices.v21.stubs.replication.DocumentStub

class DocumentStubBuilder extends AbstractAngelStubBuilder<Document, DocumentStub> {

	@Override
	protected DocumentStub createFullStub(Document entity) {
		def stub = new DocumentStub()
		
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		
		stub.setName(entity.getName())
		stub.setWebVisible(entity.getWebVisibility().getDatabaseValue())
		stub.setFileUUID(entity.getFileUUID())
		stub.setDescription(entity.getDescription())
		stub.setRemoved(entity.getIsRemoved())
		stub.setShared(entity.getIsShared())
		
		return stub
	}
}
