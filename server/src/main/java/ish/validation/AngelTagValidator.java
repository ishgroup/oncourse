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

/**
 * Created by anarut on 11/21/16.
 */
public class AngelTagValidator {

    private ITag tag;
    private ValidationResult validationResult;

    private AngelTagValidator() {
    }

    public static AngelTagValidator valueOf(ITag tag, ValidationResult validationResult) {
        AngelTagValidator angelTagValidator = new AngelTagValidator();
        angelTagValidator.tag = tag;
        angelTagValidator.validationResult = validationResult;
        return angelTagValidator;
    }

    public void validate() {
        TagNameValidator.valueOf(tag, validationResult).validate();

        if (Boolean.TRUE.equals(tag.getIsVocabulary())) {
            if (tag.isTagAncestor(tag.getParentTag())) {
                validationResult.addFailure(new ValidationFailure(tag, ITag.PARENT_TAG_KEY, "The selected parent is child of this Web Page."));
            }
            if (this == tag.getParentTag()) {
                validationResult.addFailure(new ValidationFailure(tag, ITag.PARENT_TAG_KEY, "Invalid parent Web Page."));
            }
        } else {
            if (tag.getTagRequirements().size() > 0) {
                validationResult.addFailure(new ValidationFailure(tag, ITag.TAG_REQUIREMENTS_KEY, "Only parent tags could have requirements."));
            }
        }

    }
}
