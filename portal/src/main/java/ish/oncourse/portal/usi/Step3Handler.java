package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.preference.PreferenceController;

import java.util.Map;

import static ish.oncourse.model.auto._Contact.*;
import static ish.oncourse.model.auto._Student.SPECIAL_NEEDS_PROPERTY;
import static ish.oncourse.portal.usi.UsiController.Step.step3Done;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step3Handler extends AbstractStepHandler {


    @Override
    public Result getValue() {
        if (result.isEmpty()) {

            Contact contact = getUsiController().getContact();
            Student student = contact.getStudent();

            addValue(Value.valueOf(IS_MALE_PROPERTY, contact.getIsMale(), true));
            addValue(Value.valueOf(STREET_PROPERTY, contact.getStreet(), isRequiredField(STREET_PROPERTY)));
            addValue(Value.valueOf(SUBURB_PROPERTY, contact.getSuburb(), isRequiredField(SUBURB_PROPERTY)));
            addValue(Value.valueOf(POSTCODE_PROPERTY, contact.getPostcode(), isRequiredField(POSTCODE_PROPERTY)));
            addValue(Value.valueOf(STATE_PROPERTY, contact.getState(), isRequiredField(STATE_PROPERTY)));
            addValue(Value.valueOf(COUNTRY_PROPERTY, contact.getCountry() != null ? contact.getCountry().getName() : null, isRequiredField(COUNTRY_PROPERTY)));
            addValue(Value.valueOf(MOBILE_PHONE_NUMBER_PROPERTY, contact.getMobilePhoneNumber(), isRequiredField(COUNTRY_PROPERTY)));
            addValue(Value.valueOf(HOME_PHONE_NUMBER_PROPERTY, contact.getHomePhoneNumber(), isRequiredField(COUNTRY_PROPERTY)));
            addValue(Value.valueOf(BUSINESS_PHONE_NUMBER_PROPERTY, contact.getBusinessPhoneNumber(), isRequiredField(COUNTRY_PROPERTY)));
            addValue(Value.valueOf(SPECIAL_NEEDS_PROPERTY, student.getSpecialNeeds(), isRequiredField(SPECIAL_NEEDS_PROPERTY)));
        }
        return result;
    }

    private boolean isRequiredField(String key) {
        return getUsiController().getContactFieldHelper().isRequiredField(PreferenceController.FieldDescriptor.valueOf(key));
    }

    @Override
    public UsiController.Step getNextStep() {
        return step3Done;
    }

    public Step3Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        Contact contact = getUsiController().getContact();
        Student student = contact.getStudent();

        handleGenderValue(contact, IS_MALE_PROPERTY);
        handleContactValue(contact, STREET_PROPERTY);
        handleContactValue(contact, SUBURB_PROPERTY);
        handleContactValue(contact, POSTCODE_PROPERTY);
        handleContactValue(contact, STATE_PROPERTY);
        handleCountryValue(contact, COUNTRY_PROPERTY);
        handleContactValue(contact, MOBILE_PHONE_NUMBER_PROPERTY);
        handleContactValue(contact, HOME_PHONE_NUMBER_PROPERTY);
        handleContactValue(contact, BUSINESS_PHONE_NUMBER_PROPERTY);
        handleContactValue(student, SPECIAL_NEEDS_PROPERTY);
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