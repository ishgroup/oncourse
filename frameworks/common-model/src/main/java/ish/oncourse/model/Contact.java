package ish.oncourse.model;

import ish.oncourse.model.auto._Contact;
import ish.oncourse.utils.PhoneValidator;
import ish.oncourse.utils.TimestampUtilities;

import java.util.Date;

public class Contact extends _Contact implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ?
				(Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

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
		if (Boolean.TRUE.equals(getIsCompany())) {
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
		if (!emailAddress
				.matches("^[\\w\\d\\-\\.]+@[\\w\\d\\-]+(\\.[\\w\\d\\-]+)+$")) {
			return "The email address does not appear to be valid.";
		}

		return null;
	}

	public String validateSuburb() {
		String suburb = getSuburb();
		if (suburb == null || "".equals(suburb)) {
			// not required
			return null;
		}
		if (suburb.split("\\d").length != 1) {
			return "A suburb name cannot contain numeric digits.";
		}
		return null;
	}

	public String validatePostcode() {
		String suburb = getPostcode();
		if (suburb == null || "".equals(suburb)) {
			// not required
			return null;
		}
		if (!suburb.matches("(\\d){4}")) {
			return "The postcode must be four digits.";
		}
		return null;
	}

	public String validateState() {
		String state = getState();
		if (state == null || "".equals(state)) {
			// not required
			return null;
		}
		if (state.length() > 20) {
			return "The name of the state cannot exceed 20 characters.";
		}
		return null;
	}

	public String validateHomePhone() {
		String homePhone = getHomePhoneNumber();
		if (homePhone == null || "".equals(homePhone)) {
			// not required
			return null;
		}
		try {
			setHomePhoneNumber(PhoneValidator.validatePhoneNumber(homePhone));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validateMobilePhone() {
		String mobilePhone = getMobilePhoneNumber();
		if (mobilePhone == null || "".equals(mobilePhone)) {
			// not required
			return null;
		}
		try {
			setMobilePhoneNumber(PhoneValidator
					.validateMobileNumber(mobilePhone));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validateBusinessPhone() {
		String businessPhone = getBusinessPhoneNumber();
		if (businessPhone == null || "".equals(businessPhone)) {
			// not required
			return null;
		}
		try {
			setBusinessPhoneNumber(PhoneValidator
					.validatePhoneNumber(businessPhone));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validateFax() {
		String fax = getFaxNumber();
		if (fax == null || "".equals(fax)) {
			// not required
			return null;
		}
		try {
			setFaxNumber(PhoneValidator.validatePhoneNumber(fax));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validatePassword() {
		String password = getPassword();
		return validatedPassword(password, false);
	}

	public String validatePasswordConfirm(String confirmPassword) {
		return validatedPassword(confirmPassword, true);
	}

	public String validateBirthDate() {
		Date birthDate = getDateOfBirth();
		if (birthDate == null) {
			return "The " + getEntityName() + "'s date of birth is required.";
		}
		int yearsBetweenDates = TimestampUtilities.yearsBetweenDates(birthDate,
				new Date());
		if (yearsBetweenDates < 17 || yearsBetweenDates > 100) {

			return "Please enter a valid date of birth";
		}
		return null;
	}

	private String validatedPassword(String aValue, boolean isConfirm) {

		String prefix = "The password" + (isConfirm ? " confirm" : "") + " ";
		int minimumPasswordChars = 4;
		if (aValue == null || aValue.length() < minimumPasswordChars) {
			return prefix + "must be at least " + minimumPasswordChars
					+ " characters long.";
		}
		if (aValue.split("\\s").length != 1) {
			return prefix + "cannot contain blank spaces.";
		}
		if (isConfirm && !aValue.equals(getPassword())) {
			return prefix + "does not match the given password.";
		}
		return null;
	}

}
