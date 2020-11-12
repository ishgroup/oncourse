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
import ish.oncourse.server.cayenne.glue._Field

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * Data collection rules {@link FieldConfigurationScheme} are made up of Fields.
 * They are ordered and grouped with headings.
 *
 * Note that some fields have special treatment in the user interface. For example
 * validation on email address or date of birth, or autocomplete on suburb and postcode.
 */
@QueueableEntity
@API
class Field extends _Field implements Queueable, Comparable<Field> {

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@Override
	@API
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return a description of the field is typically used for help or hover text in the UI
	 */
	@Nullable @Override @API
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * @return true if this field is required to have a non-null value to validate the data collection
	 */
	@Nonnull @Override @API
	Boolean getMandatory() {
		return super.getMandatory()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull @Override @API
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the name of the field is usually shown as a label in the UI
	 */
	@Nonnull @Override @API
	String getName() {
		return super.getName()
	}

	/**
	 *
	 * @return a string property key which identifies which attribute this fields points to
	 */
	@Nonnull @Override @API
	String getProperty() {
		return super.getProperty()
	}

	/**
	 * Fields are grouped into FieldConfigurations
	 *
	 * @return FieldConfiguration grouping of this field
	 */
	@Nonnull @Override @API
	FieldConfiguration getFieldConfiguration() {
		return super.getFieldConfiguration()
	}

	/**
	 *
	 * @return the FieldHeading under which this Field is grouped
	 */
	@Nullable @Override @API
	FieldHeading getFieldHeading() {
		return super.getFieldHeading()
	}

	@Override
	int compareTo(Field o) {
		return getOrder() - o.getOrder()
	}
}



