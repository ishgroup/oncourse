/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormErrors } from "redux-form";
import { getDeepValue } from "../common";

export * from "./emailsValidation";
export * from "./urlValidation";
export * from "./phonesValidation";
export * from "./mandatoryValidation";
export * from "./uniqueNamesValidation";
export * from "./tagNamesValidation";
export * from "./datesValidation";
export * from "./numbersValidation";
export * from "./fieldArrayAsyncValidation";
export * from "./vetFieldsValidation";

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

  return value.replace(new RegExp(pattern),"").trim() ? "Value has invalid format" : undefined;
};
