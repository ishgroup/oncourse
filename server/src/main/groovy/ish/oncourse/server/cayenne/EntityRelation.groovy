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

import ish.common.types.EntityRelationIdentifier
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._EntityRelation

import javax.annotation.Nonnull

/**
 * Entity relation describes generic link between two records in onCourse.
 */
//TODO docs
@API
@QueueableEntity
class EntityRelation extends _EntityRelation implements Queueable {



	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return angel ID of the entity record on the left side of the relation
	 */
	@Nonnull
	@API
	@Override
	Long getFromEntityAngelId() {
		return super.getFromEntityAngelId()
	}

	/**
	 * @return type of entity record on the left side of the relation
	 */
	@Nonnull
	@API
	@Override
	EntityRelationIdentifier getFromEntityIdentifier() {
		return super.getFromEntityIdentifier()
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
	 * @return angel ID of entity record on the right side of the relation
	 */
	@Nonnull
	@API
	@Override
	Long getToEntityAngelId() {
		return super.getToEntityAngelId()
	}

	/**
	 * @return type of entity record on the right side of the relation
	 */
	@Nonnull
	@API
	@Override
	EntityRelationIdentifier getToEntityIdentifier() {
		return super.getToEntityIdentifier()
	}


}
