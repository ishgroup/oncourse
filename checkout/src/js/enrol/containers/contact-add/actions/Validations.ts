import {validateEmail} from "../../../../common/utils/FormControlsValidation";

export const validateContact = (values: any): any => {
  const errors: any = {};

  if (!values.firstName) {
    errors.firstName = "The student's first name is required.";
  }
  if (!values.lastName) {
    errors.lastName = "The student's last name is required.";
  }
  if (!values.email) {
    errors.email = "The student's email is required.";
  } else {
    errors.email = validateEmail(values.email);
  }
  return errors;
};

export const validateCompany = (values: any): any => {
  const errors: any = {};

  if (!values.lastName) {
    errors.lastName = "The company name is required.";
  }
  if (!values.email) {
    errors.email = "The company email is required.";
  } else {
    errors.email = validateEmail(values.email);
  }
  return errors;
};
