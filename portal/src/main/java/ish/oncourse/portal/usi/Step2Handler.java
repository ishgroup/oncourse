package ish.oncourse.portal.usi;

import ish.common.types.StudyReason;
import ish.oncourse.model.Country;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.selectutils.BooleanSelection;
import ish.oncourse.util.MessagesNamingConvention;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ish.oncourse.model.Student.LABOUR_FORCE_STATUS_PROPERTY;
import static ish.oncourse.model.auto._Student.*;
import static ish.oncourse.portal.usi.UsiController.Step.step3;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step2Handler extends AbstractStepHandler {
    private static final Pattern ENROLMENT_KEY_PATTERN = Pattern.compile("^enrolment-([0-9]+)$");

    @Override
    public Result getValue() {
        if (result.isEmpty()) {

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

            List<Enrolment> enrolments = getUsiController().getVETEnrolments();
            for (Enrolment enrolment : enrolments) {
                addValue(getEnrolmentValue(enrolment));
            }
        }
        return result;
    }

    private Value getEnrolmentValue(Enrolment enrolment) {
        String key = String.format("enrolment-%d", enrolment.getId());

        StudyReason[] studyReasons = StudyReason.values();
        String value = null;
        Value[] options = new Value[studyReasons.length];
        for (int i = 0; i < studyReasons.length; i++) {
            StudyReason studyReason = studyReasons[i];
            options[i] = Value.valueOf(studyReason.name(), studyReason.getDisplayName());
            if (studyReason.getDatabaseValue().equals(enrolment.getReasonForStudy()))
                value = studyReason.name();
        }
        return Value.valueOf(key, value, null, options);
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

    @Override
    public UsiController.Step getNextStep() {
        return step3;
    }

    public Step2Handler handle(Map<String, Value> input) {
        this.inputValues = input;
        Student student = getUsiController().getContact().getStudent();

        Set<String> keys = inputValues.keySet();
        for (String key : keys) {
            switch (key) {
                case ENGLISH_PROFICIENCY_PROPERTY:
                case INDIGENOUS_STATUS_PROPERTY:
                case HIGHEST_SCHOOL_LEVEL_PROPERTY:
                case PRIOR_EDUCATION_CODE_PROPERTY:
                case LABOUR_FORCE_STATUS_PROPERTY:
                case DISABILITY_TYPE_PROPERTY:
                    handleEnumValue(student, key);
                    break;
                case IS_STILL_AT_SCHOOL_PROPERTY:
                    handleBooleanSelectionValue(student, IS_STILL_AT_SCHOOL_PROPERTY);
                    break;
                case COUNTRY_OF_BIRTH_PROPERTY:
                    handleCountryValue(student, COUNTRY_OF_BIRTH_PROPERTY);
                    break;
                case LANGUAGE_HOME_PROPERTY:
                    handleLanguageValue(student, LANGUAGE_HOME_PROPERTY);
                    break;
                case YEAR_SCHOOL_COMPLETED_PROPERTY:
                    handleYearSchoolCompleted(student, YEAR_SCHOOL_COMPLETED_PROPERTY);
                    break;
                default:
                    Matcher matcher = ENROLMENT_KEY_PATTERN.matcher(key);
                    if (matcher.matches()) {
                        handleEnrolmentValue(key, Long.valueOf(matcher.group(1)));
                    }
            }
        }
        return this;
    }

    private void handleYearSchoolCompleted(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            String sYear = (String) value.getValue();
            if (StringUtils.isNumeric(sYear)) {
                int year = Integer.valueOf(sYear);
                Calendar calendar = Calendar.getInstance();
                if (year <= calendar.get(Calendar.YEAR)) {
                    student.setYearSchoolCompleted(Integer.valueOf(sYear));
                } else {
                    result.addValue(Value.valueOf(key, value.getValue(), getUsiController().getMessages().format("message-yearSchoolCompletedShouldBeInPast")));
                    result.setHasErrors(true);
                }
            } else {
                result.addValue(Value.valueOf(key, value.getValue(), avetmissMessages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, key))));
                result.setHasErrors(true);
            }

        }
    }

    private void handleLanguageValue(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            Language language = getUsiController().getLanguageService().getLanguageByName((String) value.getValue());
            if (language != null) {
                student.setLanguageHome(student.getObjectContext().localObject(language));
                result.addValue(value);
            } else {
                result.addValue(Value.valueOf(key, value.getValue(), avetmissMessages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, key))));
                result.setHasErrors(true);
            }
        }
    }

    private void handleBooleanSelectionValue(Student student, String key) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            student.writeProperty(value.getKey(), BooleanSelection.valueOf((String) value.getValue()).getValue());
            result.addValue(value);
        }
    }

    private void handleEnrolmentValue(String key, Long enrolmentId) {
        Value value = inputValues.get(key);
        if (value != null && value.getValue() != null) {
            Enrolment enrolment = Cayenne.objectForPK(getUsiController().getContact().getObjectContext(), Enrolment.class, enrolmentId);
            if (enrolment != null && enrolment.getStudent().getId().equals(getUsiController().getContact().getStudent().getId())) {
                enrolment.setReasonForStudy(StudyReason.valueOf((String) value.getValue()).getDatabaseValue());
            }
        }
    }
}