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
package ish.oncourse.common.field;

import static ish.oncourse.common.field.PropertyGetSetFactory.*;

public enum FieldProperty {

	FIRST_NAME(ContextType.CONTACT, "First name", "firstName"),
	LAST_NAME(ContextType.CONTACT, "Last name", "lastName"),
	EMAIL_ADDRESS(ContextType.CONTACT, "Email address", "emailAddress"),
	MIDDLE_NAME(ContextType.CONTACT, "Middle name", "middleName"),
	HONORIFIC(ContextType.CONTACT, "Honorific", "honorific"),
	TITLE(ContextType.CONTACT, "Title", "title"),
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
	IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY(ContextType.CONTACT, "E-mail", "isMarketingViaEmailAllowed", "accept email marketing material"),
	IS_MARKETING_VIA_POST_ALLOWED_PROPERTY(ContextType.CONTACT, "Post", "isMarketingViaPostAllowed", "accept postal marketing material"),
	IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY(ContextType.CONTACT, "SMS", "isMarketingViaSMSAllowed", "accept sms marketing material"),

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

	NET_PROMOTER_SCORE(ContextType.SURVEY, "Net promoter score", "netPromoterScore"),
	COURSE_SCORE(ContextType.SURVEY, "Course score", "courseScore"),
	VENUE_SCORE(ContextType.SURVEY, "Venue score", "venueScore"),
	TUTOR_SCORE(ContextType.SURVEY, "Tutor score", "tutorScore"),
	COMMENT(ContextType.SURVEY, "Comment", "comment"),

	DETAIL(ContextType.WAITING_LIST, "Student notes","detail"),
	STUDENTS_COUNT(ContextType.WAITING_LIST,"Number of students", "potentialStudents"),
	

	CUSTOM_FIELD_CONTACT(ContextType.CONTACT, "Custom Field ", CUSTOM_FIELD_PROPERTY_PATTERN   + ContextType.CONTACT.getIdentifier()),
	CUSTOM_FIELD_STUDENT(ContextType.STUDENT, "Custom field ", CUSTOM_FIELD_PROPERTY_PATTERN + ContextType.STUDENT.getIdentifier()),
	CUSTOM_FIELD_COURSE(ContextType.COURSE, "Custom field ", CUSTOM_FIELD_PROPERTY_PATTERN  + ContextType.COURSE.getIdentifier()),
	CUSTOM_FIELD_ENROLMENT(ContextType.ENROLMENT, "Custom field ", CUSTOM_FIELD_PROPERTY_PATTERN + ContextType.ENROLMENT.getIdentifier()),
	CUSTOM_FIELD_APPLICATION(ContextType.APPLICATION, "Custom field ", CUSTOM_FIELD_PROPERTY_PATTERN  + ContextType.APPLICATION.getIdentifier()),
	CUSTOM_FIELD_WAITING_LIST(ContextType.WAITING_LIST, "Custom field ", CUSTOM_FIELD_PROPERTY_PATTERN  + ContextType.WAITING_LIST.getIdentifier()),
	CUSTOM_FIELD_SURVEY(ContextType.SURVEY, "Custom field ", CUSTOM_FIELD_PROPERTY_PATTERN  + ContextType.SURVEY.getIdentifier()),
	CUSTOM_FIELD_COURSE_CLASS(ContextType.COURSE_CLASS, "Custom field", CUSTOM_FIELD_PROPERTY_PATTERN + ContextType.COURSE_CLASS.getIdentifier()),
	CUSTOM_FIELD_ARTICLE(ContextType.ARTICLE, "Custom field", CUSTOM_FIELD_PROPERTY_PATTERN + ContextType.ARTICLE.getIdentifier()),
	CUSTOM_FIELD_MEMBERSHIP(ContextType.MEMBERSHIP, "Custom field", CUSTOM_FIELD_PROPERTY_PATTERN + ContextType.MEMBERSHIP.getIdentifier()),
	CUSTOM_FIELD_VOUCHER(ContextType.VOUCHER, "Custom field", CUSTOM_FIELD_PROPERTY_PATTERN + ContextType.VOUCHER.getIdentifier()),

	TAG(ContextType.CONTACT, "Tag field", TAG_PATTERN),
	S_TAG_GROUP(ContextType.CONTACT, "Single tag field", TAG_S_PATTERN),
	M_TAG_GROUP(ContextType.CONTACT, "Multiple tag filed", TAG_M_PATTERN),
	MAILING_LIST(ContextType.CONTACT, "Mailing List Field", MAILING_LIST_FIELD_PATTERN);

	private ContextType contextType;
	private String displayName;
	private String key;
	private String defaultDescription;

	public String getDisplayName() {
		return displayName;
	}

	public String getKey() {
		return key;
	}

	public ContextType getContextType() {
		return contextType;
	}

	public String getDefaultDescription() {
		return defaultDescription;
	}

	private FieldProperty(ContextType contextType, String displayName, String key) {
		this.contextType = contextType;
		this.displayName = displayName;
		this.key = key;
		this.defaultDescription = null;
	}

	private FieldProperty(ContextType contextType, String displayName, String key, String defaultDescription) {
		this.contextType = contextType;
		this.displayName = displayName;
		this.key = key;
		this.defaultDescription = defaultDescription;
	}

	public static FieldProperty getByKey(String key) {
		if (key.startsWith(CUSTOM_FIELD_PROPERTY_PATTERN)) {
			String contextIdentifier = key.split("\\.")[1];
			ContextType context = ContextType.getByIdentifier(contextIdentifier);
			switch (context) {
				case CONTACT:
					return CUSTOM_FIELD_CONTACT;
				case STUDENT:
                    return CUSTOM_FIELD_STUDENT;
                case COURSE:
                    return CUSTOM_FIELD_COURSE;
                case ENROLMENT:
                    return CUSTOM_FIELD_ENROLMENT;
                case APPLICATION:
                    return CUSTOM_FIELD_APPLICATION;
                case WAITING_LIST:
                    return CUSTOM_FIELD_WAITING_LIST;
				case SURVEY:
					return CUSTOM_FIELD_SURVEY;
				case COURSE_CLASS:
					return CUSTOM_FIELD_COURSE_CLASS;
				case ARTICLE:
					return CUSTOM_FIELD_ARTICLE;
				case MEMBERSHIP:
					return CUSTOM_FIELD_MEMBERSHIP;
				case VOUCHER:
					return CUSTOM_FIELD_VOUCHER;
                default: throw new UnsupportedOperationException("Custom fields supported for Contact only");
			}
		} else if (key.startsWith(TAG_PATTERN)) {
			return TAG;
		} else if (key.startsWith(MAILING_LIST_FIELD_PATTERN)) {
			return MAILING_LIST;
		}

		for (FieldProperty property : FieldProperty.values()) {
			if (property.getKey().equals(key)) {
				return property;
			}
		}
		return null;
	}
}
