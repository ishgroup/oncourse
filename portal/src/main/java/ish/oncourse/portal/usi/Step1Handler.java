package ish.oncourse.portal.usi;

import ish.common.types.UsiStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.util.FormatUtils;

import java.util.Date;
import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.wait;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step1Handler extends AbstractStepHandler {

    @Override
    public Map<String, Value> getValue() {
        Date date = getUsiController().getContact().getDateOfBirth();
        Student student = getUsiController().getContact().getStudent();
        addValue(Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (date != null ?
                FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, getUsiController().getContact().getCollege().getTimeZone()).format(date) :
                null)));
        addValue(Value.valueOf(Student.USI_PROPERTY,
                student.getUsi()));
        addValue(Value.valueOf(Student.USI_STATUS_PROPERTY, student.getUsiStatus() != null ? student.getUsiStatus().name() : UsiStatus.DEFAULT_NOT_SUPPLIED));
        return value;
    }

  @Override
    public Step getNextStep() {
        return wait;
    }

    public Step1Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        handleDateOfBirth(Contact.DATE_OF_BIRTH_PROPERTY);
        handleRequiredValue(getUsiController().getContact().getStudent(), Student.USI_PROPERTY);
        return this;
    }


    protected void handleDateOfBirth( String key) {
        Value inputValue = inputValues.get(key);
        Value value = new DateOfBirthParser().parse(inputValue, getUsiController());
        result.addValue(value);
        if (value.getError() != null) {
            result.setHasErrors(true);
        }
    }
}
