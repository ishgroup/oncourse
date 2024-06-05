/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ForbiddenTagNames, Tag } from "@api/model";
import { DecoratedFormProps } from "redux-form";
import { validateForbiddenSymbols, validateSingleMandatoryField } from "../../../common/utils/validation";

const validateUniqueTagName = (tag: Tag, allTags: Tag[], message: string = "The tag name is not unique within its parent tag") => (
  allTags.some(t => t.id !== tag.id && tag.name === t.name)
    ? message
    : undefined
);

const validateTagUrlPath = (tag: Tag, allTags: Tag[], message: string = "The tag short name is not unique within its parent tag") => {
  if (!tag.urlPath) return undefined;

  if (ForbiddenTagNames[tag.urlPath.toLowerCase()]) {
    return "This name is reserved by the onCourse system and cannot be used.";
  }

  if (tag.urlPath.includes('"')) {
    return "Double quotes are not permitted in the short name.";
  }

  if (tag.urlPath.includes("+")) {
    return "Plus sign is not permitted in the short name.";
  }

  if (tag.urlPath.includes("\\")) {
    return "Backslash is not permitted in the short name.";
  }

  if (tag.urlPath.includes("/")) {
    return "Forward slash is not permitted in the short name.";
  }

  if (tag.urlPath.startsWith("template-") || tag.urlPath.match(/\/[0-9]/)) {
    return "This short name is not allowed.";
  }

  return allTags.some(t => t.id !== tag.id && tag.urlPath === t.urlPath) ? message : undefined;
};

const validateChildTags = (tag: Tag, errors: Tag[]) => {
  tag.childTags?.forEach((t, index, all) => {
    const nameError = validateSingleMandatoryField(t.name)
      || validateUniqueTagName(t, all);

    if (nameError) {
      errors[index] = {
        name: nameError
      };
    }

    const pathError = validateTagUrlPath(t, all);

    if (pathError) {
      errors[index] ? errors[index].urlPath = pathError : errors[index] = {
        urlPath: pathError
      };
    }

    if (t.childTags?.length) {
      const childErrors = [];
      validateChildTags(t, childErrors);
      if (childErrors.length) {
        errors[index] ? errors[index].childTags = childErrors : errors[index] = {
          childTags: childErrors
        };
      }
    }
  });
};

export const validateTagsForm = (values: Tag, props: DecoratedFormProps<Tag, { tags: Tag[] }>): any => {
  const errors: Tag = {};
  const { tags = [] } = props;

  if (!values) {
    return errors;
  }

  const nameError = validateSingleMandatoryField(values.name)
    || validateForbiddenSymbols(values.name)
    || validateUniqueTagName(values, tags, "Name is not unique");

  if (nameError) {
    errors.name = nameError;
  }

  const pathError = validateTagUrlPath(values, tags, "Short name is not unique");

  if (pathError) {
    errors.urlPath = pathError;
  }

  const childErrors = [];
  validateChildTags(values, childErrors);
  if (childErrors.length) {
    errors.childTags = childErrors;
  }

  return errors;
};