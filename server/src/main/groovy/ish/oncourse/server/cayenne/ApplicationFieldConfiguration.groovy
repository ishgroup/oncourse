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

import ish.common.types.FieldConfigurationType
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._ApplicationFieldConfiguration

import javax.annotation.Nonnull
/**
 * A field configuration which is used to collect application information
 */
@API
class ApplicationFieldConfiguration extends _ApplicationFieldConfiguration {



	@Override
	FieldConfigurationType getType() {
		return FieldConfigurationType.APPLICATION
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull @Override @API
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull @Override @API
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return a sorted list of all fields attached to this field configuration
	 */
	@Nonnull @Override @API
	List<Field> getFields() {
		return super.getFields()
	}

	/**
	 * @return the name of this field configuration. Not shown to students anywhere.
	 */
	@Nonnull @Override @API
	String getName() {
		return super.getName()
	}

	/**
	 * @return a list of all headings attached to this field configuration
	 */
	@Nonnull @Override @API
	List<FieldHeading> getFieldHeadings() {
		return super.getFieldHeadings()
	}
}



