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


public enum USIVerificationStatus {

	VALID("Valid"),
	INVALID("Invalid"),
	DEACTIVATED("Deactivated");

	private String stringValue;

	private USIVerificationStatus(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public static USIVerificationStatus fromString(String stringValue) {
		for (USIVerificationStatus usiStatus : values()) {
			if (usiStatus.getStringValue().equals(stringValue)) {
				return usiStatus;
			}
		}

		return null;
	}
}
