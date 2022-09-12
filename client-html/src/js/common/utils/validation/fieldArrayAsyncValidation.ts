/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ServerResponse } from "../../../model/common/apiHandlers";
import { Dispatch } from "redux";
import instantFetchErrorHandler from "../../api/fetch-errors-handlers/InstantFetchErrorHandler";

export interface FieldArrayFieldMeta {
  array: string;
  index: string;
  field?: string;
}

export const getFieldArrayFieldMeta = (field: string): FieldArrayFieldMeta => {
  const fieldMatch = field.match(/(\w+)\[(\d+)]\.(\w+)/);

  if (fieldMatch) {
    return {
      array: fieldMatch[1],
      index: fieldMatch[2],
      field: fieldMatch[3]
    };
  }

  return null;
};

export const highlightFieldArrayFieldError = (array: string, index: string, field: string, errorMessage: string) => {
  const idexNum = Number(index);

  const error = { [array]: Array(idexNum).fill(null) };

  error[array][idexNum] = { [field]: errorMessage };

  throw error;
};

export const asyncValidateFieldArrayFieldCallback = (
  response: ServerResponse,
  meta: FieldArrayFieldMeta,
  dispatch: Dispatch<any>
) => {
  if (response && response.status && response.status === 400 && response.data) {
    highlightFieldArrayFieldError(
      meta.array,
      meta.index,
      response.data.propertyName || meta.field,
      response.data.errorMessage
    );
  }
  instantFetchErrorHandler(dispatch, response);
  throw "Something went wrong";
};

export const instantAsyncValidateFieldArrayItemCallback = (array: string, index: number, response: ServerResponse) => {
  if (response && response.status && response.status === 400 && response.data) {
    const error = { [array]: Array(index).fill(null) };

    error[array][index] = {
      [response.data.propertyName]: response.data.errorMessage
    };
    return error;
  }
  return undefined;
};