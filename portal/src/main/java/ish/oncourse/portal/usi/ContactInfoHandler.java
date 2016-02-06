package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.portal.usi.handler.CountryOfBirthHandler;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;

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

			addValue(Value.valueOf(STREET.getName(), contact.getStreet(), true, contact.getStreet() != null));
			addValue(Value.valueOf(SUBURB.getName(), contact.getSuburb(), true, contact.getSuburb() != null));
			addValue(Value.valueOf(POSTCODE.getName(), contact.getPostcode(), true, contact.getPostcode() != null));

			Date date = contact.getDateOfBirth();
			addValue(Value.valueOf(Contact.DATE_OF_BIRTH.getName(), (date != null ?
					FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, getUsiController().getContact().getCollege().getTimeZone()).format(date) :
					null), true, date != null));

			addValue(Value.valueOf(COUNTRY_OF_BIRTH.getName(), (student.getCountryOfBirth() != null ? student.getCountryOfBirth().getName() : null),
					true, student.getCountryOfBirth() != null));
			addValue(Value.valueOf(TOWN_OF_BIRTH.getName(), student.getTownOfBirth(), true, student.getTownOfBirth() != null));

			addValue(Value.valueOf(IS_MALE.getName(), contact.getIsMale(), true));
			addValue(Value.valueOf(STATE.getName(), contact.getState(), isRequiredField(STATE.getName())));
			addValue(Value.valueOf(COUNTRY.getName(), contact.getCountry() != null ? contact.getCountry().getName() : null, isRequiredField(COUNTRY.getName())));
			addValue(Value.valueOf(MOBILE_PHONE_NUMBER.getName(), contact.getMobilePhoneNumber(), isRequiredField(COUNTRY.getName())));
			addValue(Value.valueOf(HOME_PHONE_NUMBER.getName(), contact.getHomePhoneNumber(), isRequiredField(COUNTRY.getName())));
			addValue(Value.valueOf(BUSINESS_PHONE_NUMBER.getName(), contact.getBusinessPhoneNumber(), isRequiredField(COUNTRY.getName())));
			addValue(Value.valueOf(SPECIAL_NEEDS.getName(), student.getSpecialNeeds(), isRequiredField(SPECIAL_NEEDS.getName())));
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

		handleContactValue(contact, STREET.getName());
		handleContactValue(contact, SUBURB.getName());
		handleContactValue(contact, POSTCODE.getName());
		handleDateOfBirth(Contact.DATE_OF_BIRTH.getName());
		CountryOfBirthHandler.valueOf(student, Student.COUNTRY_OF_BIRTH.getName(), inputValues, result, getUsiController()).handle();
		handleContactValue(student, TOWN_OF_BIRTH.getName());

		handleGenderValue(contact, IS_MALE.getName());
		handleContactValue(contact, STATE.getName());
		handleCountryValue(contact, COUNTRY.getName());
		handleContactValue(contact, MOBILE_PHONE_NUMBER.getName());
		handleContactValue(contact, HOME_PHONE_NUMBER.getName());
		handleContactValue(contact, BUSINESS_PHONE_NUMBER.getName());
		handleContactValue(student, SPECIAL_NEEDS.getName());
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