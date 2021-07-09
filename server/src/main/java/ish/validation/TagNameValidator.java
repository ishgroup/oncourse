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

import ish.oncourse.common.NodeInterface;
import ish.oncourse.server.cayenne.Tag;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by anarut on 11/21/16.
 */
public class TagNameValidator {


    private Tag tag;
    private ValidationResult validationResult;

    private TagNameValidator() {
    }

    public static TagNameValidator valueOf(Tag tag, ValidationResult validationResult) {
        TagNameValidator tagNameValidator = new TagNameValidator();
        tagNameValidator.tag = tag;
        tagNameValidator.validationResult = validationResult;
        return tagNameValidator;
    }

    public void validate() {
        if (StringUtils.isBlank(tag.getName())) {
            // name cannot be empty
            validationResult.addFailure(new ValidationFailure(tag, Tag.NAME_KEY, "Please specify the name."));
        } else {
            for (Tag sibling : tag.getSiblings()) {
                if (tag.getName().equals(sibling.getName())) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.NAME_KEY, "The tag name is not unique within its parent tag."));
                }
                if (tag.getShortName() != null && tag.getShortName().equals(sibling.getShortName())) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "The node name is not unique."));
                }
            }
        }

        if (tag.getShortName() != null) {
            if (StringUtils.isBlank(tag.getShortName())) {
                validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "The short name cannot be empty."));
            } else {
                if (NodeInterface.NODE_NAMES_FORBIDDEN_CASE_INSENSITIVE.contains(tag.getShortName().toLowerCase())) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "Your chosen tag name is reserved by the onCourse system and cannot be used."));
                } else if (tag.getShortName().contains("\"")) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "Double quotes are not permitted in the short name."));
                } else if (tag.getShortName().contains("+")) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "Plus sign is not permitted in the short name."));
                } else if (tag.getShortName().contains("\\")) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "Backslash is not permitted in the short name."));
                } else if (tag.getShortName().contains("/")) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "Forward slash is not permitted in the short name."));
                } else if (Pattern.matches(NodeInterface.ILLEGAL_NODE_SHORTNAME_PATTERN, tag.getShortName())) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "This short name is not allowed."));
                } else if (tag.getShortName().startsWith("template-")) {
                    validationResult.addFailure(new ValidationFailure(tag, Tag.SHORT_NAME_KEY, "This short name is not allowed."));
                }
            }
        }
    }
}
