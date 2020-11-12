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

import ish.oncourse.cayenne.Taggable
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.cayenne.glue._ContactTagRelation

import javax.annotation.Nonnull

/**
 * A persistent class mapped as "ContactTagRelation" Cayenne entity.
 */
class ContactTagRelation extends _ContactTagRelation {



	/**
	 * @see TagRelation#getTaggableClassesIdentifier()
	 */
	@Nonnull
	@Override
	TaggableClasses getTaggableClassesIdentifier() {
		return TaggableClasses.CONTACT
	}

	/**
	 * @see TagRelation#getTaggedRelation()
	 */
	@Nonnull
	@Override
	Taggable getTaggedRelation() {
		return super.getTaggedContact()
	}

	/**
	 * @see TagRelation#setTaggedRelation(Taggable)
	 */
	@Override
	void setTaggedRelation(Taggable object) {
		super.setTaggedContact((Contact) object)
	}
}
