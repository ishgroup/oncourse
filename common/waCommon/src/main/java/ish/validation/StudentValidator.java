package ish.validation;

import ish.oncourse.cayenne.StudentInterface;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static ish.validation.StudentErrorCode.*;

public class StudentValidator implements Validator<StudentErrorCode> {

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
            } else if (thisYear - givenYear > 100) {
                result.put(StudentInterface.YEAR_SCHOOL_COMPLETED_KEY, yearSchoolCompletedWithinLast100Year);
            }
        }
    }
}
