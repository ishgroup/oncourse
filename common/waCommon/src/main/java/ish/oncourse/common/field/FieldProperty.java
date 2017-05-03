/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common.field;

public enum FieldProperty {
	
	FIRST_NAME(ContextType.CONTACT, "First name", "firstName"),
	LAST_NAME(ContextType.CONTACT, "Last name", "lastName"),
	EMAIL_ADDRESS(ContextType.CONTACT, "Email address", "emailAddress"),
	STREET(ContextType.CONTACT, "Street", "street"),
	SUBURB(ContextType.CONTACT, "Suburb", "suburb"),
	POSTCODE(ContextType.CONTACT, "Postcode", "postcode"),
	STATE(ContextType.CONTACT, "State", "state"),
	COUNTRY(ContextType.CONTACT, "Country", "country"),
	HOME_PHONE_NUMBER(ContextType.CONTACT, "Home phone number", "homePhoneNumber"),
	BUSINESS_PHONE_NUMBER(ContextType.CONTACT, "Business phone number", "businessPhoneNumber"),
	FAX_NUMBER(ContextType.CONTACT, "Fax number", "faxNumber"),
	MOBILE_PHONE_NUMBER(ContextType.CONTACT, "Mobile phone number", "mobilePhoneNumber"),
	DATE_OF_BIRTH(ContextType.CONTACT, "Date of Birth", "dateOfBirth"),
	ABN(ContextType.CONTACT, "ABN", "abn"),
	IS_MALE(ContextType.CONTACT, "Gender", "isMale" ),
	
	CITIZENSHIP(ContextType.STUDENT, "Citizenship", "citizenship" ),
	COUNTRY_OF_BIRTH(ContextType.STUDENT, "Country of birth", "countryOfBirth" ),
	LANGUAGE_HOME(ContextType.STUDENT, "Language spoken at Home", "languageHome" ),
	YEAR_SCHOOL_COMPLETED(ContextType.STUDENT, "Year school completed", "yearSchoolCompleted"),
	ENGLISH_PROFICIENCY(ContextType.STUDENT, "English proficiency", "englishProficiency" ),
	INDIGENOUS_STATUS(ContextType.STUDENT, "Indigenous Status", "indigenousStatus" ),
	HIGHEST_SCHOOL_LEVEL(ContextType.STUDENT, "Highest school level", "highestSchoolLevel" ),
	IS_STILL_AT_SCHOOL(ContextType.STUDENT, "Still at school", "isStillAtSchool"),
	PRIOR_EDUCATION_CODE(ContextType.STUDENT, "Prior education code", "priorEducationCode" ),
	LABOUR_FORCE_STATUS(ContextType.STUDENT, "Labour force status", "labourForceStatus" ),
	DISABILITY_TYPE(ContextType.STUDENT, "Disability type", "disabilityType" ),
	SPECIAL_NEEDS(ContextType.STUDENT, "Special needs", "specialNeeds"),

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
