import moment from "moment";
import { URL_REGEX } from "../../constants/Validation";

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

  const errorMessage = "The email address does not appear to be valid";

  if (!value.includes("@")) {
    return errorMessage;
  }
  const splitted = value.split("@");
  return /\../.test(splitted[splitted.length - 1]) ? undefined : errorMessage;
};

export const validateURL = value => {
  return !value || URL_REGEX.test(value) ? undefined : "URL has invalid format";
};
