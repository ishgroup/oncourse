/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common.field;


public enum FieldProperty {
	
	DATE_OF_BIRTH(ContextType.CONTACT, "Date of Birth", "dateOfBirth", java.util.Date.class),
	EMAIL_ADDRESS(ContextType.CONTACT, "Email address", "emailAddress", String.class),
	CITIZENSHIP(ContextType.STUDENT, "Citizenship", "citizenship", String.class),
	
	CUSTOM_FIELD(ContextType.CONTACT, "Custom Field ", "customField.key", String.class);

	private ContextType contextType;
	private String displayName;
	private String key;
	private Class parameterType;
	
	public String getDisplayName() {
		return displayName;
	}

	public String getKey() {
		return key;
	}
	
	public ContextType getContextType() {
		return contextType;
	}

	public Class getParameterType() {
		return parameterType;
	}
	
	private FieldProperty(ContextType contextType, String displayName, String key, Class parameterType) {
		this.contextType = contextType;
		this.displayName = displayName;
		this.key = key;
		this.parameterType = parameterType;
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
