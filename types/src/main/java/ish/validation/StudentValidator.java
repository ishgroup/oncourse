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

import ish.oncourse.cayenne.StudentInterface;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static ish.validation.StudentErrorCode.*;

public class StudentValidator implements Validator<StudentErrorCode> {

    public static final int MIN_YEAR_SCHOOL_COMPLETED = 1940;

    private StudentInterface student;
    private Map<String, StudentErrorCode> result;

    private StudentValidator() {
    }

    public static StudentValidator valueOf(StudentInterface student) {
        StudentValidator studentValidator = new StudentValidator();
        studentValidator.student = student;
        studentValidator.result = new HashMap<>();

        return studentValidator;
    }

    @Override
    public Map<String, StudentErrorCode> validate() {
        validateYearSchoolCompleted();

        return result;
    }

    private void validateYearSchoolCompleted() {
        if (student.getYearSchoolCompleted() != null) {
            final int givenYear = student.getYearSchoolCompleted();
            final int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            if (givenYear > thisYear) {
                result.put(StudentInterface.YEAR_SCHOOL_COMPLETED_KEY, yearSchoolCompletedInFuture);
            } else if (givenYear < MIN_YEAR_SCHOOL_COMPLETED) {
                result.put(StudentInterface.YEAR_SCHOOL_COMPLETED_KEY, yearSchoolCompletedBefore1940);
            }
        }
    }
}
