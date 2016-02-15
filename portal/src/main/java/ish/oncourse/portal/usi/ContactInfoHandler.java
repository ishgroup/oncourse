package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.portal.usi.handler.CountryOfBirthHandler;
import ish.oncourse.services.preference.PreferenceController;

import java.util.Date;
import java.util.Map;

import static ish.oncourse.model.auto._Contact.*;
import static ish.oncourse.model.auto._Student.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ContactInfoHandler extends AbstractStepHandler {

	@Override
	public Result getValue() {
		if (result.isEmpty()) {

			Contact contact = getUsiController().getContact();
			Student student = contact.getStudent();


			if (contact.getStreet() == null) {
				addValue(Value.valueOf(STREET.getName(), contact.getStreet(), true, false));
			}

			if (contact.getSuburb() == null) {
				addValue(Value.valueOf(SUBURB.getName(), contact.getSuburb(), true, false));
			}

			if (contact.getPostcode() == null) {
				addValue(Value.valueOf(POSTCODE.getName(), contact.getPostcode(), true, false));
			}

			Date date = contact.getDateOfBirth();
			if (date == null) {
				addValue(Value.valueOf(Contact.DATE_OF_BIRTH.getName(), null, true, false));
			}

			if (student.getCountryOfBirth() == null) {
				addValue(Value.valueOf(COUNTRY_OF_BIRTH.getName(), null, true, false));
			}

			if (student.getTownOfBirth() == null) {
				addValue(Value.valueOf(TOWN_OF_BIRTH.getName(), student.getTownOfBirth(), true, false));
			}

			if (contact.getIsMale() == null) {
				addValue(Value.valueOf(IS_MALE.getName(), contact.getIsMale(), true));
			}

			if (contact.getState() == null) {
				addValue(Value.valueOf(STATE.getName(), contact.getState(), isRequiredField(STATE.getName())));
			}

			if (contact.getCountry() == null) {
				addValue(Value.valueOf(COUNTRY.getName(), null, isRequiredField(COUNTRY.getName())));
			}

			if (contact.getMobilePhoneNumber() == null) {
				addValue(Value.valueOf(MOBILE_PHONE_NUMBER.getName(), contact.getMobilePhoneNumber(), isRequiredField(COUNTRY.getName())));
			}

			if (contact.getHomePhoneNumber() == null) {
				addValue(Value.valueOf(HOME_PHONE_NUMBER.getName(), contact.getHomePhoneNumber(), isRequiredField(COUNTRY.getName())));
			}

			if (contact.getBusinessPhoneNumber() == null) {
				addValue(Value.valueOf(BUSINESS_PHONE_NUMBER.getName(), contact.getBusinessPhoneNumber(), isRequiredField(COUNTRY.getName())));
			}

			if (student.getSpecialNeeds() == null) {
				addValue(Value.valueOf(SPECIAL_NEEDS.getName(), student.getSpecialNeeds(), isRequiredField(SPECIAL_NEEDS.getName())));
			}
		}
		return result;
	}

	private boolean isUsiSpecificField(String key) {
		return (STREET.getName().equals(key) ||
				SUBURB.getName().equals(key) ||
				POSTCODE.getName().equals(key) ||
				COUNTRY_OF_BIRTH.getName().equals(key) ||
				TOWN_OF_BIRTH.getName().equals(key));
	}

	private boolean isRequiredField(String key) {
		return isUsiSpecificField(key) ||
				getUsiController().getContactFieldHelper().isRequiredField(PreferenceController.FieldDescriptor.valueOf(key), getUsiController().getContact());
	}

	@Override
	public Step getNextStep() {
		return Step.avetmissInfo;
	}

	public ContactInfoHandler handle(Map<String, Value> input) {
		this.inputValues = input;
		Contact contact = getUsiController().getContact();
		Student student = contact.getStudent();

		if (contact.getStreet() == null) {
			handleContactValue(contact, STREET.getName());
		}

		if (contact.getSuburb() == null) {
			handleContactValue(contact, SUBURB.getName());
		}

		if (contact.getPostcode() == null) {
			handleContactValue(contact, POSTCODE.getName());
		}

		if (contact.getDateOfBirth() == null) {
			handleDateOfBirth(Contact.DATE_OF_BIRTH.getName());
		}

		if (student.getCountryOfBirth() == null) {
			CountryOfBirthHandler.valueOf(student, Student.COUNTRY_OF_BIRTH.getName(), inputValues, result, getUsiController()).handle();
		}

		if (student.getTownOfBirth() == null) {
			handleContactValue(student, TOWN_OF_BIRTH.getName());
		}

		if (contact.getIsMale() == null) {
			handleGenderValue(contact, IS_MALE.getName());
		}

		if (contact.getState() == null) {
			handleContactValue(contact, STATE.getName());
		}

		if (contact.getCountry() == null) {
			handleCountryValue(contact, COUNTRY.getName());
		}

		if (contact.getMobilePhoneNumber() == null) {
			handleContactValue(contact, MOBILE_PHONE_NUMBER.getName());
		}

		if (contact.getHomePhoneNumber() == null) {
			handleContactValue(contact, HOME_PHONE_NUMBER.getName());
		}

		if (contact.getBusinessPhoneNumber() == null) {
			handleContactValue(contact, BUSINESS_PHONE_NUMBER.getName());
		}

		if (contact.getStudent().getSpecialNeeds() == null) {
			handleContactValue(student, SPECIAL_NEEDS.getName());
		}
		return this;
	}

	private void handleCountryValue(Contact contact, String key) {
		Value inputValue = inputValues.get(key);

		if (inputValue != null && inputValue.getValue() != null) {
			super.handleCountryValue(contact, key);
		} else {
			if (isRequiredField(key)) {
				result.addValue(Value.valueOf(key, null, getUsiController().getMessages().format("message-fieldRequired")));
				result.setHasErrors(true);
			}
		}
	}


	private void handleGenderValue(Contact contact, String key) {
		Value inputValue = inputValues.get("isMale");

		if (inputValue == null) {
			result.addValue(Value.valueOf(key, null, getUsiController().getMessages().format("message-fieldRequired")));
			result.setHasErrors(true);
		} else {
			if (inputValue.getValue().equals("male")) {
				contact.setIsMale(Boolean.TRUE);
			} else if (inputValue.getValue().equals("female")) {
				contact.setIsMale(Boolean.FALSE);
			}
			result.addValue(Value.valueOf(key, contact.getIsMale()));
		}
	}

	private <T> void handleContactValue(T entity, String key) {
		if (isRequiredField(key)) {
			handleRequiredValue(entity, key);
		} else {
			handleValue(entity, key);
		}
	}
}