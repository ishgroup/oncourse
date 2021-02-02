import moment from "moment";
import {EMAIL_REGEX, URL_REGEX} from "../../constants/Validation";

export const validateDate = value => {
  return !value || moment(value, 'DD/MM/YYYY',true).isValid() ? undefined : "Date has invalid format";
};

export const validateDateTime = value => {
  return !value || moment(value, 'DD/MM/YYYY hh:mm',true).isValid() ? undefined : "Date has invalid format";
};

export const validateEmail = value => {
  return !value || EMAIL_REGEX.test(value) ? undefined : "The email address does not appear to be valid";
};

export const validateURL = value => {
  return !value || URL_REGEX.test(value) ? undefined : "URL has invalid format";
};
