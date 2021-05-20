import moment from "moment";
import {URL_REGEX} from "../../constants/Validation";

export const validateDate = value => {
  return !value || moment(value, 'DD/MM/YYYY',true).isValid() ? undefined : "Date has invalid format";
};

export const validateDateTime = value => {
  return !value || moment(value, 'DD/MM/YYYY HH:mm',true).isValid() ? undefined : "Date has invalid format";
};

export const validateEmail = value => {
  if (!value) {
    return undefined;
  }

  const errorMessage = "Please enter valid email address";

  if (!value.includes("@") || value.match(/\.@|@\./g)) {
    return errorMessage;
  }
  const splitted = value.split("@");
  return /\../.test(splitted[splitted.length - 1]) ? undefined : errorMessage;
};

export const validateURL = value => {
  return !value || URL_REGEX.test(value) ? undefined : "URL has invalid format";
};

export const validatePattern = (value, pattern) => {
  if (!value || !pattern) return undefined;

  return value.replace(new RegExp(pattern),"").trim() ? "Value has invalid format" : undefined;
};
