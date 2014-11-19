package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.util.FormatUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step1FailedHandler extends Step1Handler{

    @Override
    public Map<String, Value> getValue() {

        Map<String, Value> values = new HashMap<>();

        Contact contact = getUsiController().getContact();
        Date date = getUsiController().getContact().getDateOfBirth();
        values.put(Contact.DATE_OF_BIRTH_PROPERTY, Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (Object) (date != null ?
                FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, getUsiController().getContact().getCollege().getTimeZone()).format(date) :
                null)));
        values.put(Contact.GIVEN_NAME_PROPERTY, Value.valueOf(Contact.GIVEN_NAME_PROPERTY, (Object) contact.getGivenName()));
        values.put(Contact.FAMILY_NAME_PROPERTY, Value.valueOf(Contact.FAMILY_NAME_PROPERTY, (Object) contact.getFamilyName()));
        return values;
    }

    @Override
    public Step1FailedHandler handle(Map<String, Value> inputValues) {
        this.inputValues = inputValues;
        result = new HashMap<>();
        Value inputValue = inputValues.get(Contact.DATE_OF_BIRTH_PROPERTY);
        parseDateOfBirth(inputValue);

        inputValue = inputValues.get(Contact.GIVEN_NAME_PROPERTY);
        parseRequiredValue(Contact.GIVEN_NAME_PROPERTY, inputValue, getUsiController().getContact());

        inputValue = inputValues.get(Contact.FAMILY_NAME_PROPERTY);
        parseRequiredValue(Contact.FAMILY_NAME_PROPERTY, inputValue, getUsiController().getContact());
        return this;
    }
}
