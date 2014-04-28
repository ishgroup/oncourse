/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.DocumentVersion;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.DocumentVersionStub;

public class DocumentVersionStubBuilder extends AbstractWillowStubBuilder<DocumentVersion, DocumentVersionStub> {

	@Override
	protected DocumentVersionStub createFullStub(DocumentVersion entity) {
		DocumentVersionStub stub = new DocumentVersionStub();

		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		
		stub.setFileName(entity.getFileName());
		stub.setByteSize(entity.getByteSize());
		stub.setMimeType(entity.getMimeType());
		stub.setPixelHeight(entity.getPixelHeight());
		stub.setPixelWidth(entity.getPixelWidth());
		stub.setVersionId(entity.getVersionId());
		stub.setTimestamp(entity.getTimestamp());
		stub.setThumbnail(entity.getThumbnail());
		
		stub.setDocumentId(entity.getDocument().getId());
		
		return stub;
	}
}
