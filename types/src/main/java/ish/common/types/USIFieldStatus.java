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
package ish.common.types;

/**
 *
 */
public enum USIFieldStatus {

	/**
	 *
	 */
	MATCH("Match"),

	/**
	 *
	 */
	NO_MATCH("NoMatch");

	private String stringValue;

	private USIFieldStatus(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public static USIFieldStatus fromString(String stringValue) {
		for (USIFieldStatus fieldStatus : values()) {
			if (fieldStatus.getStringValue().equals(stringValue)) {
				return fieldStatus;
			}
		}

		return null;
	}
}
