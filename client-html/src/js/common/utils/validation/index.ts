/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormErrors } from "redux-form";
import { getDeepValue } from "../common";
import { Tag } from "@api/model";

export * from "./emailsValidation";
export * from "./urlValidation";
export * from "./phonesValidation";
export * from "./mandatoryValidation";
export * from "./uniqueNamesValidation";
export * from "./symbols";
export * from "./datesValidation";
export * from "./numbersValidation";
export * from "./fieldArrayAsyncValidation";
export * from "./vetFieldsValidation";

const isParent = (tag: Tag, ID: number) => (tag.id === ID ? true : tag.childTags.find(c => isParent(c, ID)));

export const validateTagsList = (tags: Tag[], value, allValues, props, rootEntity?) => {
  let error;

  if (!tags) {
    return error;
  }

  const rootTagsWithRequirements = {};

  tags.forEach(t => {
    const match = t.requirements.filter(r => (r.type === (rootEntity || props.rootEntity)) && (r.mandatory || r.limitToOneTag));

    if (match.length) {
      rootTagsWithRequirements[t.id] = { name: t.name, requirements: match[0] };
    }
  });

  const usedRootTags = {};

  if (value) {
    value.forEach(i => {
      const match = tags.find(t => isParent(t, i));

      if (match) {
        if (usedRootTags[match.id]) {
          usedRootTags[match.id].push(match);
        } else {
          usedRootTags[match.id] = [match];
        }
      }
    });
  }

  Object.keys(rootTagsWithRequirements).forEach(u => {
    if (!usedRootTags[u] && rootTagsWithRequirements[u].requirements.mandatory) {
      error = `The ${
        rootTagsWithRequirements[u].name
      } tag is mandatory. Modify your tag settings before removing this tag`;
    }

    if (usedRootTags[u] && rootTagsWithRequirements[u].requirements.limitToOneTag && usedRootTags[u].length > 1) {
      error = `The ${rootTagsWithRequirements[u].name} tag group can be used only once`;
    }
  });

  return error;
};

export const getFirstErrorNodePath = (errors: FormErrors, deepObj?: any, path?: string): string => {
  if (!errors) {
    return path;
  }

  const targetObj = deepObj || errors;
  let firstKeyIndex = 0;
  let key;
  let numberKey = false;

  if (Array.isArray(targetObj)) {
    firstKeyIndex = targetObj.findIndex(t => t);
    key = firstKeyIndex;
  } else {
    key = Object.keys(targetObj)[firstKeyIndex];
  }

  if (!isNaN(Number(key))) {
    key = `[${key}]`;
    numberKey = true;
  }

  path = (path ? numberKey ? path : `${path}.` : "") + key;

  const value = getDeepValue(errors, path);

  if (typeof value === "object") {
    return getFirstErrorNodePath(errors, value, path);
  }

  return path?.replace("._error", "");
};

export const validateRegex = pattern => {
  if (!pattern) return undefined;
  const parts = pattern.split('/');
  let regex = pattern;
  let options = "";
  if (parts.length > 1) {
    regex = parts[1];
    options = parts[2];
  }
  try {
    RegExp(regex, options);
    return undefined;
  } catch (e) {
    return "Please enter valid regex pattern";
  }
};

export const validatePattern = (value, pattern) => {
  if (!value || !pattern) return undefined;

  return value.replace(new RegExp(pattern), "").trim() ? "Value has invalid format" : undefined;
};

export const valiadateSelectItemAvailable = (value, items, valueKey = "value") => (items.some(i => i[valueKey] === value) ? undefined : "Selected value is not available");
