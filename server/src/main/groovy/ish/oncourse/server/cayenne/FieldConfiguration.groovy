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
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._FieldConfiguration
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull
/**
 * Data collection rules {@link FieldConfigurationScheme} comprise several FieldConfigurations:
 *
 * 1. waiting list
 * 2. enrolment
 * 3. application
 * 4. survey
 *
 * A {@link FieldConfiguration} can be reused as part of several different schemes
 */
@QueueableEntity @API
abstract class FieldConfiguration extends _FieldConfiguration implements Queueable {

	@Override
	void validateForDelete(ValidationResult validationResult) {
		if (getId() != null && getId() == -1L) {
			validationResult.addFailure(ValidationFailure.validationFailure(this, ID.getName(), "Default configuration can't be deleted."))
		}
		super.validateForDelete(validationResult)
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

	/**
	 * @return all the schemes in which this configuration is used
	 */
	@Nonnull @API
	List<FieldConfigurationScheme> getSchemes() {
		List<FieldConfigurationScheme> result = new ArrayList<>()
		for (FieldConfigurationLink link : getFieldConfigurationLink()) {
			result.add(link.getFieldConfigurationScheme())
		}
		return result
	}

	abstract FieldConfigurationType getType();
}



