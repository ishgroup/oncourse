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

  if (Array.isArray(targetObj)) {
    firstKeyIndex = targetObj.findIndex(t => t);
  }

  let key = Object.keys(targetObj)[firstKeyIndex];
  let numberKey = false;

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
