/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common.field;


public enum FieldProperty {
	
	DATE_OF_BIRTH(ContextType.CONTACT, "Date of Birth", "dateOfBirth" ),
	EMAIL_ADDRESS(ContextType.CONTACT, "Email address", "emailAddress"),
	CITIZENSHIP(ContextType.STUDENT, "Citizenship", "citizenship"),
	
	CUSTOM_FIELD(ContextType.CONTACT, "Custom Field ", "customField.key");

	private ContextType contextType;
	private String displayName;
	private String key;
	
	public String getDisplayName() {
		return displayName;
	}

	public String getKey() {
		return key;
	}
	
	public ContextType getContextType() {
		return contextType;
	}
	
	private FieldProperty(ContextType contextType, String displayName, String key) {
		this.contextType = contextType;
		this.displayName = displayName;
		this.key = key;
	}

	public static FieldProperty getByKey(String key) {
		for (FieldProperty property : FieldProperty.values()) {
			if (property.getKey().equals(key)) {
				return property;
			}
		}
		return null;
	}
}
