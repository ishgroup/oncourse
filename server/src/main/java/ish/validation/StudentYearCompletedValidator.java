/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.validation;

import ish.oncourse.server.cayenne.Student;

import java.util.Calendar;

public class StudentYearCompletedValidator {

    private Student student;
    private String propertyKey;

    private static final String TEMPLATE_NOT_EARLIER = "Year school completed if supplied should be within not earlier than %d.";
    private static final String MESSAGE_YEAR_IN_FUTURE = "Year school completed cannot be in the future if supplied.";

    private StudentYearCompletedValidator() {}

    public static StudentYearCompletedValidator valueOf(Student student, String propertyKey) {
        StudentYearCompletedValidator validator = new StudentYearCompletedValidator();
        validator.student = student;
        validator.propertyKey = propertyKey;
        return validator;
    }

    public ValidationResult validate() {
        ValidationResult validationResult = new ValidationResult();
        if (student.getYearSchoolCompleted() != null) {
            if (student.getYearSchoolCompleted() < Student.MIN_SCHOOL_COMPLETION_YEAR) {
                validationResult.addFailure(new ValidationFailure(student,
                        propertyKey,
                        String.format(TEMPLATE_NOT_EARLIER, Student.MIN_SCHOOL_COMPLETION_YEAR)));
            } else if (student.getYearSchoolCompleted() > Calendar.getInstance().get(Calendar.YEAR)) {
                validationResult.addFailure(new ValidationFailure(student, propertyKey, MESSAGE_YEAR_IN_FUTURE));
            }

        }
        return validationResult;
    }
}
