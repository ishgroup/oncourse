/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.AttachmentRelation
import ish.oncourse.webservices.v23.stubs.replication.BinaryInfoRelationStub

@CompileStatic
class AttachmentRelationStubBuilder extends AbstractAngelStubBuilder<AttachmentRelation, BinaryInfoRelationStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected BinaryInfoRelationStub createFullStub(AttachmentRelation entity) {
		def stub = new BinaryInfoRelationStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())

		if (entity.getDocument() != null) {
			stub.setDocumentId(entity.getDocument().getId())
		}

		if (entity.getDocumentVersion() != null) {
			stub.setDocumentVersionId(entity.getDocumentVersion().getId())
		}

		stub.setEntityName(entity.getEntityIdentifier())
		stub.setEntityAngelId(entity.getEntityRecordId())

		stub.setSpecialType(entity.getSpecialType() != null ? entity.getSpecialType().getDatabaseValue() : null)

		return stub
	}
}
