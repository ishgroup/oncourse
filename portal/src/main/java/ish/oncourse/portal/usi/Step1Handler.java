package ish.oncourse.portal.usi;

import ish.common.types.UsiStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.portal.services.AppModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint;
import ish.util.UsiUtil;

import java.util.Date;
import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.wait;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step1Handler extends AbstractStepHandler {

    @Override
    public Result getValue() {
        if (result.isEmpty()) {
            Date date = getUsiController().getContact().getDateOfBirth();
            Student student = getUsiController().getContact().getStudent();
            addValue(Value.valueOf(Contact.DATE_OF_BIRTH.getName(), (date != null ?
                    FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, getUsiController().getContact().getCollege().getTimeZone()).format(date) :
                    null)));
            addValue(Value.valueOf(Student.USI.getName(),
                    student.getUsi()));
            addValue(Value.valueOf(Student.USI_STATUS.getName(), student.getUsiStatus() != null ? student.getUsiStatus().name() : UsiStatus.DEFAULT_NOT_SUPPLIED));
        }
        return result;
    }

  @Override
    public Step getNextStep() {
        return wait;
    }

    public Step1Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        handleDateOfBirth(Contact.DATE_OF_BIRTH.getName());
        handleUsi();
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

    private void handleUsi()
    {
        Student student = getUsiController().getContact().getStudent();
        handleRequiredValue(getUsiController().getContact().getStudent(), Student.USI.getName());
        if (result.getValue().get(Student.USI.getName()).getError() == null
                && !TestUSIServiceEndpoint.useTestUSIEndpoint()) {
            if (!UsiUtil.validateKey(student.getUsi())) {
                result.addValue(Value.valueOf(Student.USI.getName(), student.getUsi(), getUsiController().getMessages().format("message-invalidUsiError")));
                result.setHasErrors(true);
            }
        }
    }
}
