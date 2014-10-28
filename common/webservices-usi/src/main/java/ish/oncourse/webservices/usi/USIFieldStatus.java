/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

public enum  USIFieldStatus {
	
	MATCH("Match"),
	NO_MATCH("No Match");
	
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
