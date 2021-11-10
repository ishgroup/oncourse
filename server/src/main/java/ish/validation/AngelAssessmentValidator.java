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

import ish.oncourse.server.cayenne.Assessment;
import org.apache.commons.lang3.StringUtils;

public class AngelAssessmentValidator {

    private org.apache.cayenne.validation.ValidationResult validationResult;
    private Assessment assessment;

    private AngelAssessmentValidator() {

    }

    public static AngelAssessmentValidator valueOf(org.apache.cayenne.validation.ValidationResult validationResult, Assessment assessment) {
        AngelAssessmentValidator angelAssessmentValidator = new AngelAssessmentValidator();
        angelAssessmentValidator.validationResult = validationResult;
        angelAssessmentValidator.assessment = assessment;
        return angelAssessmentValidator;
    }

    public void validate() {
        if (StringUtils.isBlank(assessment.getName())) {
            validationResult.addFailure(new ValidationFailure(assessment, Assessment.NAME_KEY, "You need to enter a name."));
        }

        if (StringUtils.isBlank(assessment.getCode())) {
            validationResult.addFailure(new ValidationFailure(assessment, Assessment.CODE_KEY, "You need to enter a code."));
        }

        if (assessment.getActive() == null) {
            validationResult.addFailure(new ValidationFailure(assessment, Assessment.ACTIVE_KEY, "Active couldn't be null."));
        }
    }
}
