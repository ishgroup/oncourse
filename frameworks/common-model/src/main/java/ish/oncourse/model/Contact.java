package ish.oncourse.model;

import ish.oncourse.model.auto._Contact;

public class Contact extends _Contact {

	public String getFullName() {
		StringBuilder buff = new StringBuilder();
		String familyName = getFamilyName();
		if (Boolean.TRUE.equals(getIsCompany())) {
			if (familyName != null) {
				buff.append(familyName);
			}

		} else {
			String givenName = getGivenName();
			if (givenName != null) {
				buff.append(givenName);
			}
			if (familyName != null) {
				if (buff.length() > 0) {
					buff.append(" ");
				}
				buff.append(familyName);
			}
		}
		return buff.toString();
	}

	private String getEntityName() {
		if (getStudent() != null) {
			return "student";
		}
		if (getIsCompany()) {
			return "company";
		}
		if (getTutor() != null) {
			return "tutor";
		}
		return "contact";
	}

	/**
	 * Validate methods; will return the string representation of error or null,
	 * if the field is valid
	 * 
	 */
	public String validateGivenName() {
		String givenName = getGivenName();
		if (!Boolean.TRUE.equals(getIsCompany())) {
			if (givenName == null || "".equals(givenName)) {
				return "The " + getEntityName() + "'s given name is required.";
			}
			if (givenName.split("\\d").length != 1) {
				return "The given name cannot contain number characters.";
			}
		}
		return null;
	}

	public String validateFamilyName() {
		String familyName = getFamilyName();
		if (familyName == null || "".equals(familyName)) {
			return "The " + getEntityName() + "'s family name is required.";
		}
		if (familyName.split("\\d").length != 1) {
			return "The family name cannot contain number characters.";
		}
		return null;
	}

	public String validateEmail() {
		String emailAddress = getEmailAddress();
		if (emailAddress == null || "".equals(emailAddress)) {
			return "The " + getEntityName() + "'s email is required.";
		}
		if (!emailAddress.matches(
				"^[\\w\\d\\-\\.]+@[\\w\\d\\-]+(\\.[\\w\\d\\-]+)+$")) {
			return "The email address does not appear to be valid.";
		}

		return null;

	}
}
