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

/**
 * This represents a validation warning which does not prevent a record from being committed.
 */
public class ValidationWarning extends ValidationFailure {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new validation warning.
	 *
	 * @param source The persistent object which caused the warning.
	 * @param property The entity model property key for the attribute which caused the validation warning.
	 * @param error Description of the warning.
	 */
	public ValidationWarning(Object source, String property, Object error) {
		super(source, property, error);
	}

	/**
	 * Create a new validation warning.
	 *
	 * @param source The persistent object which caused the warning.
	 * @param property The entity model property key for the attribute which caused the validation warning.
	 * @param error Description of the error.
	 * @return new validation warning
	 */
	public static final ValidationWarning validationWarning(Object source, String property, Object error) {
		return new ValidationWarning(source, property, error);
	}
}
