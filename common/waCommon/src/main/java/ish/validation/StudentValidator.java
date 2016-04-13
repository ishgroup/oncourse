package ish.validation;

import ish.oncourse.cayenne.StudentInterface;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static ish.validation.StudentValidationErrorCode.YEAR_SCHOOL_COMPLETED_IN_FUTURE;
import static ish.validation.StudentValidationErrorCode.YEAR_SCHOOL_COMPLETED_WITHIN_LAST_100_YEAR;

public class StudentValidator implements Validator<StudentValidationErrorCode> {

    private StudentInterface student;
    private Map<String, StudentValidationErrorCode> result;

    private StudentValidator() {
    }

    public static StudentValidator valueOf(StudentInterface student) {
        StudentValidator studentValidator = new StudentValidator();
        studentValidator.student = student;
        studentValidator.result = new HashMap<>();

        return studentValidator;
    }

    @Override
    public Map<String, StudentValidationErrorCode> validate() {
        validateYearSchoolCompleted();

        return result;
    }

    private void validateYearSchoolCompleted() {
        if (student.getYearSchoolCompleted() != null) {
            final int givenYear = student.getYearSchoolCompleted();
            final int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            if (givenYear > thisYear) {
                result.put(StudentInterface.YEAR_SCHOOL_COMPLETED_KEY, YEAR_SCHOOL_COMPLETED_IN_FUTURE);
            } else if (thisYear - givenYear > 100) {
                result.put(StudentInterface.YEAR_SCHOOL_COMPLETED_KEY, YEAR_SCHOOL_COMPLETED_WITHIN_LAST_100_YEAR);
            }
        }
    }
}
