package ish.oncourse.portal.usi;

import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Student;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MessagesNamingConvention;
import org.apache.cayenne.CayenneDataObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public abstract class AbstractStepHandler implements StepHandler {
    protected Messages avetmissMessages = MessagesImpl.forClass(AvetmissStrings.class);
    private UsiController usiController;
    protected Map<String, Value> result = Collections.emptyMap();

    protected Map<String, Value> value = Collections.emptyMap();

    protected boolean hasErrors = false;
    protected Map<String, Value> inputValues;

    public UsiController getUsiController() {
        return usiController;
    }

    public void setUsiController(UsiController usiController) {
        this.usiController = usiController;
    }

    public void init()
    {
        result = new HashMap<>();
        value = new HashMap<>();
        hasErrors = false;
    }

    @Override
    public Map<String, Value> getResult() {
        return result;
    }

    @Override
    public Map<String, Value> getValue() {
        return value;
    }

    protected  void addValue(Value value) {
        this.value.put(value.getKey(), value);
    }

    protected <T> void handleRequiredValue(T entity, String key) {
        Value inputValue = inputValues.get(key);
        if (inputValue == null || inputValue.getValue() == null) {
            result.put(key, Value.valueOf(key, null, getUsiController().getMessages().format("message-fieldRequired")));
            hasErrors = true;
        } else {
            handleValue(entity, key);
        }
    }

    protected <T> void handleValue(T entity, String key) {
        Value inputValue = inputValues.get(key);
        if (inputValue != null && inputValue.getValue() != null) {
            try {
                Object value = inputValue.getValue();
                BeanUtils.setProperty(entity, key, value);
                result.put(key, Value.valueOf(key, inputValue.getValue()));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    protected <T extends CayenneDataObject> void handleCountryValue(T entity, String key) {
        try {
            Value value = inputValues.get(key);
            if (value != null && value.getValue() != null) {
                Country country = getUsiController().getCountryService().getCountryByName((String) value.getValue());
                if (country != null) {
                    BeanUtils.setProperty(entity, key, entity.getObjectContext().localObject(country));
                    result.put(key, value);
                } else {
                    result.put(key, Value.valueOf(key, value.getValue(), avetmissMessages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, key))));
                    hasErrors = true;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
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
                return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, null, controller.getMessages().format("message-fieldRequired"));
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
                        return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, null, controller.getMessages().format("message-dateOfBirthShouldBeInPast"));
                    if (date.before(MIN_DATE_OF_BIRTH))
                        return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, null, controller.getMessages().format("message-dateOfBirthWrongFormat"));

                    controller.getContact().setDateOfBirth(date);
                    return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (Object) FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, timeZone).format(date));
                } catch (ParseException e) {
                    return Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, null, controller.getMessages().format("message-dateOfBirthWrongFormat"));
                }
            }
        }
    }
}
