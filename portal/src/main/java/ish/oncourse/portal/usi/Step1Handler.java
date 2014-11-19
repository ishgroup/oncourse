package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.wait;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step1Handler extends AbstractStepHandler {

    @Override
    public Map<String, Value> getValue() {
        Map<String, Value> values = new HashMap<>();
        Date date = getUsiController().getContact().getDateOfBirth();
        values.put(Contact.DATE_OF_BIRTH_PROPERTY, Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (Object) (date != null ?
                FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, getUsiController().getContact().getCollege().getTimeZone()).format(date) :
                null)));
        values.put(Student.USI_PROPERTY, Value.valueOf(Student.USI_PROPERTY, (Object) getUsiController().getContact().getStudent().getUsi()));
        return values;
    }

  @Override
    public Step getNextStep() {
        return wait;
    }

    public Step1Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        parse();
        return this;
    }


    protected void parse() {
        result = new HashMap<>();
        Value inputValue = inputValues.get(Contact.DATE_OF_BIRTH_PROPERTY);
        parseDateOfBirth(inputValue);

        inputValue = inputValues.get(Student.USI_PROPERTY);
        parseRequiredValue(Student.USI_PROPERTY, inputValue, getUsiController().getContact().getStudent());
    }

    private void parseUsi(Value inputValue) {
        if (inputValue == null || inputValue.getValue() == null) {
            result.put(Student.USI_PROPERTY, Value.valueOf(Student.USI_PROPERTY, getUsiController().getMessages().format("message-usiRequired")));
            hasErrors = true;
        } else {
            getUsiController().getContact().getStudent().setUsi((String) inputValue.getValue());
            result.put(Student.USI_PROPERTY, Value.valueOf(inputValue.getKey(), inputValue.getValue()));
        }
    }

    protected void parseDateOfBirth(Value inputValue) {
        Value value = new DateOfBirthParser().parse(inputValue, getUsiController());
        result.put(Contact.DATE_OF_BIRTH_PROPERTY, value);
        if (value.getError() != null) {
            hasErrors = true;
        }
    }


    public static class DateOfBirthParser {
        public static final Date MIN_DATE_OF_BIRTH;

        static{
            try {
                MIN_DATE_OF_BIRTH = DateUtils.truncate(new SimpleDateFormat(FormatUtils.DATE_FIELD_PARSE_FORMAT).parse("01/01/1900"),
                        Calendar.YEAR);
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }
        public Value parse(Value inputValue, UsiController controller) {
            if (inputValue == null || inputValue.getValue() == null) {
                return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, controller.getMessages().format("message-fieldRequired"));
            } else {
                /**
                 * the format is used to parse string value which an user puts in date field.
                 * It uses "yy" format for year because the the format parses years like 11, 73, 85 correctly. For example:
                 * if an user enters 1/1/73 it means 01/01/1973 but not 01/01/0073 which it would be got when it uses format yyyy
                 */
                try {
                    Contact contact = controller.getContact();
                    String timeZone = contact.getCollege().getTimeZone();
                    Date date = FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_PARSE_FORMAT, timeZone).parse(inputValue.getValue().toString());

                    if (date.after(new Date()))
                        return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, controller.getMessages().format("message-dateOfBirthShouldBeInPast"));
                    if (date.before(MIN_DATE_OF_BIRTH))
                        return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, controller.getMessages().format("message-dateOfBirthWrongFormat"));

                    controller.getContact().setDateOfBirth(date);
                    return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (Object) FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, timeZone).format(date));
                } catch (ParseException e) {
                    return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, controller.getMessages().format("message-dateOfBirthWrongFormat"));
                }
            }
        }
    }
}
