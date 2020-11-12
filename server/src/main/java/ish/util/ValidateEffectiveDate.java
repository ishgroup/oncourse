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
package ish.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ValidateEffectiveDate {

	private String lockedFromString;
	private Object effectiveDate;

	private ValidateEffectiveDate() {}

	public static ValidateEffectiveDate valueOf(String lockedFromString, Object effectiveDate) {
		ValidateEffectiveDate validator = new ValidateEffectiveDate();
		validator.lockedFromString = lockedFromString;
		validator.effectiveDate = effectiveDate;
		return validator;
	}

	public boolean validate() {
		LocalDate lockedFromLocal = LocalDateUtils.stringToValue(lockedFromString, LocalDateUtils.formatter);

		LocalDate effectiveDateLocal;

		if (effectiveDate instanceof Date) {
			effectiveDateLocal = ((Date) effectiveDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} else if (effectiveDate instanceof LocalDate) {
			effectiveDateLocal = (LocalDate) effectiveDate;
		} else {
			throw new IllegalArgumentException(String.format("Can not handle effective date with %s type", effectiveDate.getClass().getName()));
		}

		return effectiveDateLocal.compareTo(lockedFromLocal) > 0;
	}
}
