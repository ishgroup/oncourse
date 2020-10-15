/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Tag
import ish.oncourse.webservices.v21.stubs.replication.TagStub

/**
 */
class TagStubBuilder extends AbstractAngelStubBuilder<Tag, TagStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected TagStub createFullStub(final Tag entity) {
		final def stub = new TagStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setDetailTextile(entity.getContents())
		stub.setModified(entity.getModifiedOn())
		stub.setName(entity.getName())
		stub.setNodeType(entity.getNodeType().getDatabaseValue())
		stub.setShortName(entity.getShortName())
		stub.setWebVisible(entity.getIsWebVisible())
		stub.setTagGroup(entity.isTagGroup())
		stub.setWeighting(entity.getWeight())
		stub.setColour(entity.getColour())
		if (entity.getSpecialType() != null) {
			stub.setSpecialType(entity.getSpecialType().getDatabaseValue())
		}
		if (entity.getParentTag() != null) {
			stub.setParentId(entity.getParentTag().getId())
		}
		return stub
	}

}
