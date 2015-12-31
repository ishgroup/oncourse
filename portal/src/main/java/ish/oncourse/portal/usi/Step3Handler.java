package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.preference.PreferenceController;

import java.util.Map;

import static ish.oncourse.model.auto._Contact.*;
import static ish.oncourse.model.auto._Student.SPECIAL_NEEDS;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step3Handler extends AbstractStepHandler {


    @Override
    public Result getValue() {
        if (result.isEmpty()) {

            Contact contact = getUsiController().getContact();
            Student student = contact.getStudent();

            addValue(Value.valueOf(IS_MALE.getName(), contact.getIsMale(), true));
            addValue(Value.valueOf(STREET.getName(), contact.getStreet(), true));
            addValue(Value.valueOf(SUBURB.getName(), contact.getSuburb(), true));
            addValue(Value.valueOf(POSTCODE.getName(), contact.getPostcode(), true));
            addValue(Value.valueOf(STATE.getName(), contact.getState(), isRequiredField(STATE.getName())));
            addValue(Value.valueOf(COUNTRY.getName(), contact.getCountry() != null ? contact.getCountry().getName() : null, isRequiredField(COUNTRY.getName())));
            addValue(Value.valueOf(MOBILE_PHONE_NUMBER.getName(), contact.getMobilePhoneNumber(), isRequiredField(COUNTRY.getName())));
            addValue(Value.valueOf(HOME_PHONE_NUMBER.getName(), contact.getHomePhoneNumber(), isRequiredField(COUNTRY.getName())));
            addValue(Value.valueOf(BUSINESS_PHONE_NUMBER.getName(), contact.getBusinessPhoneNumber(), isRequiredField(COUNTRY.getName())));
            addValue(Value.valueOf(SPECIAL_NEEDS.getName(), student.getSpecialNeeds(), isRequiredField(SPECIAL_NEEDS.getName())));
        }
        return result;
    }

    private boolean isRequiredField(String key) {

        return getUsiController().getContactFieldHelper().isRequiredField(PreferenceController.FieldDescriptor.valueOf(key),
                getUsiController().getContact())
                ||
                (STREET.getName().equals(key) || SUBURB.getName().equals(key) || POSTCODE.getName().equals(key));
    }

    @Override
    public Step getNextStep() {
        return Step.step3Done;
    }

    public Step3Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        Contact contact = getUsiController().getContact();
        Student student = contact.getStudent();

        handleGenderValue(contact, IS_MALE.getName());
        handleContactValue(contact, STREET.getName());
        handleContactValue(contact, SUBURB.getName());
        handleContactValue(contact, POSTCODE.getName());
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