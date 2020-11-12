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

import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.cayenne.glue._UnavailableRuleRelation

import javax.annotation.Nonnull
import java.util.Date

/**
 * A persistent class mapped as "UnavailableRuleRelation" Cayenne entity.
 */
abstract class UnavailableRuleRelation extends _UnavailableRuleRelation {



	/**
	 * @return the date and time this record was created
	 */
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Nonnull
	@Override
	String getEntityIdentifier() {
		return super.getEntityIdentifier()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	@Nonnull
	@Override
	UnavailableRule getRule() {
		return super.getRule()
	}

	abstract void setRelatedObject(CayenneDataObject relatedObject);
}
