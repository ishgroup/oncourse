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
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._CustomField

import javax.annotation.Nonnull

/**
 * A CustomField value. Only String is currently supported.
 */
@API
@QueueableEntity
abstract class CustomField extends _CustomField implements Queueable, CustomFieldTrait {

	abstract void setRelatedObject(ExpandableTrait relatedObject);

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the stored value
	 */
	@API
	@Override
	String getValue() {
		return super.getValue()
	}



	/**
	 * @return the type custom field
	 */
	@Nonnull
	@API
	@Override
	CustomFieldType getCustomFieldType() {
		return super.getCustomFieldType()
	}

	/**
	 * @return the contact record for which this custom field applies
	 */
	@Nonnull
	abstract ExpandableTrait getRelatedObject();

}
