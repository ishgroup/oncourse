/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.AttachmentInfoVisibility
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Document
import ish.oncourse.webservices.v23.stubs.replication.DocumentStub

class DocumentUpdater extends AbstractAngelUpdater<DocumentStub, Document> {

	@Override
	protected void updateEntity(DocumentStub stub, Document entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		entity.setWebVisibility(TypesUtil.getEnumForDatabaseValue(stub.getWebVisible(), AttachmentInfoVisibility.class))
		entity.setName(stub.getName())
		entity.setFileUUID(stub.getFileUUID())
		entity.setIsRemoved(stub.isRemoved())
		entity.setIsShared(stub.isShared())
		entity.setDescription(stub.getDescription())
	}
}
