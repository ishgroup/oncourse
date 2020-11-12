/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._ContactAttachmentRelation

import javax.annotation.Nonnull

/**
 * A persistent class mapped as "ContactAttachmentRelation" Cayenne entity.
 */
class ContactAttachmentRelation extends _ContactAttachmentRelation {



	/**
	 * @see AttachmentRelation#getAttachedRelation()
	 */
	@Nonnull
	@Override
	AttachableTrait getAttachedRelation() {
		return super.getAttachedContact()
	}

	/**
	 * @see AttachmentRelation#setAttachedRelation(AttachableTrait)
	 */
	@Override
	void setAttachedRelation(AttachableTrait attachable) {
		super.setAttachedContact((Contact) attachable)
	}

	/**
	 * @see AttachmentRelation#getEntityIdentifier()
	 */
	@Nonnull
	@Override
	String getEntityIdentifier() {
		return Contact.class.getSimpleName()
	}
}
