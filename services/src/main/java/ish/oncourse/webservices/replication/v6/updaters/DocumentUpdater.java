/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v6.updaters;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Document;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.DocumentStub;

public class DocumentUpdater extends AbstractWillowUpdater<DocumentStub, Document> {

	@Override
	protected void updateEntity(DocumentStub stub, Document entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setWebVisibility(TypesUtil.getEnumForDatabaseValue(stub.getWebVisible(), AttachmentInfoVisibility.class));
		entity.setName(stub.getName());
		entity.setFileUUID(stub.getFileUUID());
		entity.setIsRemoved(stub.isRemoved());
		entity.setIsShared(stub.isShared());
		entity.setDescription(stub.getDescription());
	}
}
