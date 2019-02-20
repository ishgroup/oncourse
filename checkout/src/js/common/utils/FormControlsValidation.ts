import moment from "moment";

export const validateDate = value => {
  return moment(value, 'DD/MM/YYYY',true).isValid() ? undefined : "Date has invalid format";
};