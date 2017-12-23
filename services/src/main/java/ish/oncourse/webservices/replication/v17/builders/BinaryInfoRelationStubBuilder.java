/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.BinaryInfoRelationStub;

public class BinaryInfoRelationStubBuilder extends AbstractWillowStubBuilder<BinaryInfoRelation, BinaryInfoRelationStub> {
	@Override
	protected BinaryInfoRelationStub createFullStub(BinaryInfoRelation entity) {
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setEntityName(entity.getEntityIdentifier());
		stub.setEntityAngelId(entity.getEntityAngelId());
		stub.setEntityWillowId(entity.getEntityWillowId());
		stub.setDocumentId(entity.getDocument().getId());
		if (entity.getDocumentVersion() != null) {
			stub.setDocumentVersionId(entity.getDocumentVersion().getId());
		}
		stub.setSpecialType(entity.getSpecialType() != null ? entity.getSpecialType().getDatabaseValue() : null);
		
		return stub;
	}
}
