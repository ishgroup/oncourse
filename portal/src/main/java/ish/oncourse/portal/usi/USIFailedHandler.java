package ish.oncourse.portal.usi;

import ish.common.types.UsiStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.util.FormatUtils;

import java.util.Date;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class USIFailedHandler extends USIHandler {

    @Override
    public Result getValue() {

        if (result.isEmpty()) {

            Contact contact = getUsiController().getContact();
            Student student = contact.getStudent();
            String timeZone = getUsiController().getContact().getCollege().getTimeZone();
            Date date = getUsiController().getContact().getDateOfBirth();
            addValue(Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (date != null ?
                    FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, timeZone).format(date) :
                    null)));
            addValue(Value.valueOf(Contact.GIVEN_NAME_PROPERTY, contact.getGivenName()));
            addValue(Value.valueOf(Contact.FAMILY_NAME_PROPERTY, contact.getFamilyName()));
            addValue(Value.valueOf(Student.USI_PROPERTY,
                    student.getUsi()));
            addValue(Value.valueOf(Student.USI_STATUS_PROPERTY, student.getUsiStatus() != null ? student.getUsiStatus().name() : UsiStatus.DEFAULT_NOT_SUPPLIED));
        }
        return result;
    }

    @Override
    public USIFailedHandler handle(Map<String, Value> inputValues) {
        this.inputValues = inputValues;

        handleDateOfBirth(Contact.DATE_OF_BIRTH_PROPERTY);

        handleRequiredValue(getUsiController().getContact(), Contact.GIVEN_NAME_PROPERTY);
        handleRequiredValue(getUsiController().getContact(), Contact.FAMILY_NAME_PROPERTY);

        if (this.inputValues.containsKey(Student.USI_PROPERTY)) {
            handleRequiredValue(getUsiController().getContact().getStudent(), Student.USI_PROPERTY);
        }
        return this;
    }
}
