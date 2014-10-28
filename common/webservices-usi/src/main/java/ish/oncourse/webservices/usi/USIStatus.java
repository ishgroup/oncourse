/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

public enum USIStatus {
	
	VALID("Valid"),
	INVALID("Invalid"),
	DEACTIVATED("Deactivated");
	
	private String stringValue;
	
	private USIStatus(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public static USIStatus fromString(String stringValue) {
		for (USIStatus usiStatus : values()) {
			if (usiStatus.getStringValue().equals(stringValue)) {
				return usiStatus;
			}
		}
		
		return null;
	}
}
