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

package ish.validation;

import org.apache.cayenne.validation.SimpleValidationFailure;

/**
 * This represents a validation failure which prevents a record from being committed.
 */
public class ValidationFailure extends SimpleValidationFailure {

	private static final long serialVersionUID = 1L;
	private String property;

	/**
	 * Create a new validation failure.
	 *
	 * @param source The persistent object which caused the failure.
	 * @param property The entity model property key for the attribute which caused the validation failure.
	 * @param error Description of the error.
	 */
	public ValidationFailure(Object source, String property, Object error) {
		super(source, error);
		this.property = property;
	}

	/**
	 * Get the entity model property key for the attribute which caused the validation failure.
	 *
	 * @return property key
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * Create a new validation failure.
	 *
	 * @param source The persistent object which caused the failure.
	 * @param property The entity model property key for the attribute which caused the validation failure.
	 * @param error Description of the error.
	 * @return new validation failure
	 */
	public static final ValidationFailure validationFailure(Object source, String property, Object error) {
		return new ValidationFailure(source, property, error);
	}

	@Override
	public String toString() {
		return "(" + this.property + ") " + super.toString();
	}
}
