package ish.oncourse.portal.usi;

import ish.common.types.*;
import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.selectutils.BooleanSelection;
import ish.oncourse.util.MessagesNamingConvention;
import org.apache.cayenne.CayenneDataObject;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.model.auto._Student.*;
import static ish.oncourse.portal.usi.UsiController.Step.step3;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step2Handler extends AbstractStepHandler {

    private Messages avetmissMessages = MessagesImpl.forClass(AvetmissStrings.class);;

    @Override
    public Map<String, Value> getValue() {
        Map<String, Value> values = new HashMap<>();

        Student student = getUsiController().getContact().getStudent();
        Country country = student.getCountryOfBirth();
        if (country != null) {
            values.put(Student.COUNTRY_OF_BIRTH_PROPERTY, Value.valueOf(Student.COUNTRY_OF_BIRTH_PROPERTY, country.getName()));
        }

        Language language = student.getLanguageHome();
        if (language != null) {
            values.put(Student.LANGUAGE_HOME_PROPERTY, Value.valueOf(Student.LANGUAGE_HOME_PROPERTY, language.getName()));
        }

        Integer year = student.getYearSchoolCompleted();
        if (year != null) {
            values.put(Student.LANGUAGE_HOME_PROPERTY, Value.valueOf(Student.LANGUAGE_HOME_PROPERTY, year));
        }

        values.put(ENGLISH_PROFICIENCY_PROPERTY, getEnumValue(ENGLISH_PROFICIENCY_PROPERTY, student, AvetmissStudentEnglishProficiency.values()));
        values.put(INDIGENOUS_STATUS_PROPERTY, getEnumValue(INDIGENOUS_STATUS_PROPERTY, student, AvetmissStudentIndigenousStatus.values()));
        values.put(HIGHEST_SCHOOL_LEVEL_PROPERTY, getEnumValue(HIGHEST_SCHOOL_LEVEL_PROPERTY, student, AvetmissStudentSchoolLevel.values()));
        values.put(PRIOR_EDUCATION_CODE_PROPERTY, getEnumValue(PRIOR_EDUCATION_CODE_PROPERTY, student, AvetmissStudentPriorEducation.values()));
        values.put(LABOUR_FORCE_TYPE_PROPERTY, getEnumValue(LABOUR_FORCE_TYPE_PROPERTY, student, AvetmissStudentLabourStatus.values()));
        values.put(DISABILITY_TYPE_PROPERTY, getEnumValue(DISABILITY_TYPE_PROPERTY, student, AvetmissStudentDisabilityType.values()));
        values.put(IS_STILL_AT_SCHOOL_PROPERTY, getBooleanSelectionValue(IS_STILL_AT_SCHOOL_PROPERTY, student));
        return values;

    }

    private Value getBooleanSelectionValue(String key, Student student) {
        BooleanSelection value = BooleanSelection.valueOf((Boolean) student.readProperty(key));
        BooleanSelection[] enumValues = BooleanSelection.values();
        Value[] options = new Value[BooleanSelection.values().length];
        for (int i = 0; i < enumValues.length; i++) {
            BooleanSelection enumValue = enumValues[i];
            options[i] = Value.valueOf(enumValue.name(), enumValue.getLabel());
        }
        return Value.valueOf(key, value.name(), null, options);
    }

    private Value getEnumValue(String key, CayenneDataObject entity, Enum[] enumValues) {
        Enum property = (Enum) entity.readProperty(key);
        Object value = null;
        if (property != null) {
            value = property.name();
        }

        Value[] options = new Value[enumValues.length];
        for (int i = 0; i < enumValues.length; i++) {
            Enum enumValue = enumValues[i];
            if (enumValue instanceof DisplayableExtendedEnumeration) {
                options[i] = Value.valueOf(enumValue.name(), ((DisplayableExtendedEnumeration) enumValue).getDisplayName());
            }
        }
        return Value.valueOf(key, value, null, options);
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
        handleEnumValue(student, LABOUR_FORCE_TYPE_PROPERTY);
        handleEnumValue(student, DISABILITY_TYPE_PROPERTY);
        handleBooleanSelectionValue(student, IS_STILL_AT_SCHOOL_PROPERTY);
        handleCountryValue(student, COUNTRY_OF_BIRTH_PROPERTY);
        handleLanguageValue(student, LANGUAGE_HOME_PROPERTY);
        return this;
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

    private void handleCountryValue(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            Country country = getUsiController().getCountryService().getCountryByName((String) value.getValue());
            if (country != null) {
                student.setCountryOfBirth(student.getObjectContext().localObject(country));
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
                student.writeProperty(value.getKey(), MethodUtils.invokeExactStaticMethod(propertyClass, "valueOf", (String) value.getValue()));
                result.put(key, value);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}