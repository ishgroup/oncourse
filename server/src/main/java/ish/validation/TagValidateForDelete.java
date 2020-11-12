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

import ish.messaging.ITag;
import org.apache.cayenne.validation.ValidationResult;

public class TagValidateForDelete {

    private ITag tag;
    private ValidationResult validationResult;

    private TagValidateForDelete() {

    }

    public static TagValidateForDelete valueOf(ITag tag, ValidationResult validationResult) {
        TagValidateForDelete tagValidateForDelete = new TagValidateForDelete();
        tagValidateForDelete.tag = tag;
        tagValidateForDelete.validationResult = validationResult;
        return tagValidateForDelete;
    }

    public void validate() {
        if (tag.getSpecialType() != null) {
            String message;
            switch (tag.getSpecialType()) {
                case SUBJECTS:
                    message = "This tag group represents the categories of courses on your web site and cannot be deleted.";
                    break;
                case HOME_WEBPAGE:
                    message = "This page represents the home page of your web site and cannot be deleted.";
                    break;
                case PAYROLL_WAGE_INTERVALS:
                    message = "This tag group is required for the onCourse tutor pay feature.";
                    break;
                case ASSESSMENT_METHOD:
                    message = "This tag group is required for the assessments.";
                    break;
                default:
                    throw new IllegalArgumentException("Unknown special type for tag");
            }

            validationResult.addFailure(ValidationFailure.validationFailure(tag, ITag.NAME_KEY, message));
        }
    }
}
