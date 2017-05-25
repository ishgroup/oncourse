import {isNil} from "lodash";
import {isUndefined} from "util";
import {CommonError} from "../../model/common/CommonError";
import {ValidationError} from "../../model/common/ValidationError";
import {AxiosResponse} from "axios";

export const isValidationError = (error: any): boolean => {
  return error instanceof ValidationError || !(isNil(error) || isUndefined(error.formErrors) && isUndefined(error.fieldsErrors));
};

export const isCommonError = (error: any): boolean => {
  return error instanceof CommonError || !(isNil(error) || isUndefined(error.code) && isUndefined(error.message));
};

export const isPlainTextError = (error: any): boolean => {
  return typeof error === "string";
};

export const toValidationError = (response: AxiosResponse) => {
  let messages: ValidationError = new ValidationError();
  messages.formErrors = [];
  messages.fieldsErrors = [];
  if (isValidationError(response.data)) {
    messages = Object.assign({}, messages, response.data);
  } else if (isCommonError(response.data)) {
    messages.formErrors.push(response.data.message);
  } else if (isPlainTextError(response.data)) {
    messages.formErrors.push(response.data);
  } else {
    messages.formErrors.push("Unexpected error.")
  }
  return messages;
};