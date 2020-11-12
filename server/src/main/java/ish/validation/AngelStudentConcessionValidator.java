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

import ish.oncourse.cayenne.StudentConcessionInterface;
import org.apache.cayenne.validation.ValidationResult;

import java.util.Map;

/**
 * Created by anarut on 9/13/16.
 */
public class AngelStudentConcessionValidator {

    private StudentConcessionInterface studentConcession;

    private ValidationResult result;

    private AngelStudentConcessionValidator() {

    }

    public static AngelStudentConcessionValidator valueOf(StudentConcessionInterface studentConcession, ValidationResult result) {
        AngelStudentConcessionValidator validator = new AngelStudentConcessionValidator();
        validator.studentConcession = studentConcession;
        validator.result = result;
        return validator;
    }

    public void validate() {
        Map<String, StudentConcessionErrorCode> errors = StudentConcessionValidator.valueOf(studentConcession).validate();

        errors.entrySet().stream().map(this::createFailureBy).forEach(result::addFailure);
    }


    private ValidationFailure createFailureBy(Map.Entry<String, StudentConcessionErrorCode> error) {
        String message;

        switch (error.getValue()) {
            case concessionTypeNeedToBeProvided:
                message = "You need to enter a concession type.";
                break;
            case concessionNumberNeedToBeProvided:
                message = "You need to enter a concession number.";
                break;
            case expiryDateNeedToBeProvided:
                message = "You need to enter a expiry date.";
                break;
            default:
                throw new IllegalArgumentException();

        }

        return ValidationFailure.validationFailure(this, error.getKey(), message);
    }


}
