package ish.validation;

import ish.oncourse.cayenne.StudentInterface;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

import java.util.Calendar;

public class StudentValidator implements Validator {

    private StudentInterface student;
    private ValidationResult result;

    private StudentValidator() {
    }

    public static StudentValidator valueOf(StudentInterface student, ValidationResult validationResult) {
        StudentValidator studentValidator = new StudentValidator();
        studentValidator.student = student;
        studentValidator.result = validationResult;

        return studentValidator;
    }

    @Override
    public ValidationResult validate() {
        validateYearSchoolCompleted();

        return result;
    }

    private void validateYearSchoolCompleted() {
        if (student.getYearSchoolCompleted() != null) {
            final int givenYear = student.getYearSchoolCompleted();
            final int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            if (givenYear > thisYear) {
                result.addFailure(new BeanValidationFailure(this, StudentInterface.YEAR_SCHOOL_COMPLETED_KEY,
                        "Year school completed cannot be in the future if supplied."));
            } else if (thisYear - givenYear > 100) {
                result.addFailure(new BeanValidationFailure(this, StudentInterface.YEAR_SCHOOL_COMPLETED_KEY,
                        "Year school completed if supplied should be within the last 100 years."));
            }
        }
    }
}
