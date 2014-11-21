package ish.oncourse.portal.usi;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.selectutils.BooleanSelection;
import ish.oncourse.util.MessagesNamingConvention;
import org.apache.cayenne.CayenneDataObject;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.model.Student.LABOUR_FORCE_STATUS_PROPERTY;
import static ish.oncourse.model.auto._Student.*;
import static ish.oncourse.portal.usi.UsiController.Step.step3;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step2Handler extends AbstractStepHandler {



    @Override
    public Map<String, Value> getValue() {
        value = new HashMap<>();

        Student student = getUsiController().getContact().getStudent();
        Country country = student.getCountryOfBirth();
        if (country != null) {
            addValue(Value.valueOf(Student.COUNTRY_OF_BIRTH_PROPERTY, country.getName()));
        }

        Language language = student.getLanguageHome();
        if (language != null) {
            addValue(Value.valueOf(Student.LANGUAGE_HOME_PROPERTY, language.getName()));
        }

        Integer year = student.getYearSchoolCompleted();
        if (year != null) {
            addValue(Value.valueOf(Student.YEAR_SCHOOL_COMPLETED_PROPERTY, year));
        }

        addValue(getEnumValue(student, ENGLISH_PROFICIENCY_PROPERTY));
        addValue(getEnumValue(student, INDIGENOUS_STATUS_PROPERTY));
        addValue(getEnumValue(student, HIGHEST_SCHOOL_LEVEL_PROPERTY));
        addValue(getEnumValue(student, PRIOR_EDUCATION_CODE_PROPERTY));
        addValue(getEnumValue(student, LABOUR_FORCE_STATUS_PROPERTY));
        addValue(getEnumValue(student, DISABILITY_TYPE_PROPERTY));
        addValue(getBooleanSelectionValue(student, IS_STILL_AT_SCHOOL_PROPERTY));
        return this.value;
    }


    private Value getBooleanSelectionValue(CayenneDataObject entity, String key) {
        BooleanSelection value = BooleanSelection.valueOf((Boolean) entity.readProperty(key));
        BooleanSelection[] enumValues = BooleanSelection.values();
        Value[] options = new Value[BooleanSelection.values().length];
        for (int i = 0; i < enumValues.length; i++) {
            BooleanSelection enumValue = enumValues[i];
            options[i] = Value.valueOf(enumValue.name(), enumValue.getLabel());
        }
        return Value.valueOf(key, value.name(), null, options);
    }

    private Value getEnumValue(CayenneDataObject entity, String key) {
        try {

            Enum property = (Enum) BeanUtilsBean2.getInstance().getPropertyUtils().getProperty(entity, key);
            Class enumClass = BeanUtilsBean2.getInstance().getPropertyUtils().getPropertyType(entity, key);
            Object value = null;
            if (property != null) {
                value = property.name();
            }

            Enum[] enumValues = (Enum[]) MethodUtils.invokeExactStaticMethod(enumClass, "values");
            Value[] options = new Value[enumValues.length];
            for (int i = 0; i < enumValues.length; i++) {
                Enum enumValue = enumValues[i];
                if (enumValue instanceof DisplayableExtendedEnumeration) {
                    options[i] = Value.valueOf(enumValue.name(), ((DisplayableExtendedEnumeration) enumValue).getDisplayName());
                }
            }
            return Value.valueOf(key, value, null, options);
        } catch (Exception e) {

            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public UsiController.Step getNextStep() {
        return step3;
    }

    public Step2Handler handle(Map<String, Value> input) {
        this.result = new HashMap<>();
        this.inputValues = input;
        Student student = getUsiController().getContact().getStudent();

        handleEnumValue(student, ENGLISH_PROFICIENCY_PROPERTY);
        handleEnumValue(student, INDIGENOUS_STATUS_PROPERTY);
        handleEnumValue(student, HIGHEST_SCHOOL_LEVEL_PROPERTY);
        handleEnumValue(student, PRIOR_EDUCATION_CODE_PROPERTY);
        handleEnumValue(student, LABOUR_FORCE_STATUS_PROPERTY);
        handleEnumValue(student, DISABILITY_TYPE_PROPERTY);
        handleBooleanSelectionValue(student, IS_STILL_AT_SCHOOL_PROPERTY);
        handleCountryValue(student, COUNTRY_OF_BIRTH_PROPERTY);
        handleLanguageValue(student, LANGUAGE_HOME_PROPERTY);
        handleYearSchoolCompleted(student, YEAR_SCHOOL_COMPLETED_PROPERTY);
        return this;
    }

    private void handleYearSchoolCompleted(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            String sYear = (String) value.getValue();
            if (StringUtils.isNumeric(sYear)) {
                student.setYearSchoolCompleted(Integer.valueOf(sYear));
            } else {
                result.put(key, Value.valueOf(key, value.getValue(), avetmissMessages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, key))));
                hasErrors = true;
            }

        }
    }

    private void handleLanguageValue(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            Language language = getUsiController().getLanguageService().getLanguageByName((String) value.getValue());
            if (language != null) {
                student.setLanguageHome(student.getObjectContext().localObject(language));
                result.put(key, value);
            } else {
                result.put(key, Value.valueOf(key, value.getValue(), avetmissMessages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, key))));
                hasErrors = true;
            }
        }
    }

    private void handleBooleanSelectionValue(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            student.writeProperty(value.getKey(), BooleanSelection.valueOf((String) value.getValue()).getValue());
            result.put(key, value);
        }
    }


    private void handleEnumValue(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            try {
                Class propertyClass = BeanUtilsBean2.getInstance().getPropertyUtils().getPropertyType(student, value.getKey());
                BeanUtilsBean2.getInstance().getPropertyUtils().setProperty(student, value.getKey(),
                        MethodUtils.invokeExactStaticMethod(propertyClass, "valueOf", (String) value.getValue()));
                result.put(key, value);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}