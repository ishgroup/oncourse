/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.Document;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.DocumentStub;

public class DocumentStubBuilder extends AbstractWillowStubBuilder<Document, DocumentStub> {

	@Override
	protected DocumentStub createFullStub(Document entity) {
		DocumentStub stub = new DocumentStub();
		
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		
		stub.setName(entity.getName());
		stub.setWebVisible(entity.getWebVisibility().getDatabaseValue());
		stub.setFileUUID(entity.getFileUUID());
		stub.setDescription(entity.getDescription());
		stub.setShared(entity.getIsShared());
		stub.setRemoved(entity.getIsRemoved());
		
		return stub;
	}
}
