/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

/**
 * @PublicApi
 */
public enum USIVerificationStatus {

	/**
	 * @PublicApi
	 */
	VALID("Valid"),

	/**
	 * @PublicApi
	 */
	INVALID("Invalid"),

	/**
	 * @PublicApi
	 */
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