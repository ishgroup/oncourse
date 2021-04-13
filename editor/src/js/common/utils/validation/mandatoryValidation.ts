import {validateEmail} from "./emailsValidation";

export const validateMultipleMandatoryFields = (values, props) => {
  const errors = {};
  props.mandatoryFields.forEach(field => {
    if (!values[field]) {
      errors[field] = "Field is mandatory";
    }
  });
  return errors;
};

export const validateSingleMandatoryField = value => {
  switch (typeof value) {
    case "string": {
      return value.trim() ? undefined : "Field is mandatory";
    }

    case "number": {
      return undefined;
    }

    case "boolean": {
      return undefined;
    }

    default: {
      return value ? undefined : "Field is mandatory";
    }
  }
};

export const validateMandatoryEmail = value => (value ? validateEmail(value) : "Field is mandatory");
