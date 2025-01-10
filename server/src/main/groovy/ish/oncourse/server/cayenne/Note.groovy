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

import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Note

import javax.annotation.Nonnull

/**
 * A Note is a freeform piece of text attached to another record. Each note is timestamped and we record who created it
 * and when they did. Normally you'd set access rights so that most users can create but not edit notes in order to create
 * a comprehensive audit trail.
 */
@API
class Note extends _Note implements ContactActivityTrait, Queueable {

	@Override
	boolean isAsyncReplicationAllowed() {
		return false
	}

	@Override
	void postAdd() {
		super.postAdd()
	}

	@Override
	protected void prePersist() {
		super.prePersist()
		if(interactionDate == null)
			interactionDate = createdOn
	}
/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Override
	String getInteractionDescription() {
		return note
	}

	@Override
	String getInteractionName() {
		return systemUser?.fullName
	}
/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 *
	 * @return the record to which this note is attached
	 */

	@Nonnull
	NotableTrait getAttachedRecord() {
		return super.getNoteRelations().getNotableEntity()
	}

	/**
	 *
	 * @return the user who last changed this note
	 */
	@API
	@Override
	SystemUser getChangedBy() {
		return super.getChangedBy()
	}

	/**
	 *
	 * @return the text of the note itself
	 */
	@Nonnull
	@API
	@Override
	String getNote() {
		return super.getNote()
	}

	/**
	 *
	 * @return the user who created this note
	 */
	@Nonnull
	@API
	@Override
	SystemUser getSystemUser() {
		return super.getSystemUser()
	}

	@Override
	Date getInteractionDate() {
		return super.getInteractionDate()
	}
}



