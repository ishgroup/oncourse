import {isNil} from "lodash";
import {isUndefined} from "util";
import {CommonError, ValidationError, CheckoutModel} from "../../model";
import {AxiosResponse} from "axios";

export const isValidationError = (error: any): boolean => {
  return error instanceof ValidationError
    || !(isNil(error) || isUndefined(error.formErrors) && isUndefined(error.fieldsErrors));
};

export const isCommonError = (error: any): boolean => {
  return error instanceof CommonError || !(isNil(error) || isUndefined(error.code) && isUndefined(error.message));
};

export const isPlainTextError = (error: any): boolean => {
  return typeof error === "string";
};

export const isCheckoutModelError = (error: any): boolean => {
  return error instanceof CheckoutModel;
}

export const commonErrorToValidationError = (error: CommonError):ValidationError => {
  const messages: ValidationError = new ValidationError();
  messages.formErrors = [];
  messages.fieldsErrors = [];
  messages.formErrors.push(error.message);
  return messages;
};

export const toValidationError = (response: AxiosResponse) => {
  let messages: ValidationError = new ValidationError();
  messages.formErrors = [];
  messages.fieldsErrors = [];

  if (!response) return messages;

  if (isValidationError(response.data)) {
    messages = Object.assign({}, messages, response.data);
  } else if (isCommonError(response.data)) {
    messages.formErrors.push(response.data.message);
  } else if (isPlainTextError(response.data)) {
    messages.formErrors.push(response.data);
  } else if (isCheckoutModelError(response.data)) {
    messages.formErrors.push(response.data.error && response.data.error.message);
  } else {
    messages.formErrors.push("Unexpected error.");
  }
  return messages;
};