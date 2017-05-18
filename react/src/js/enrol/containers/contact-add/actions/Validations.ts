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
  } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)) {
    errors.email = "The email address does not appear to be valid.";
  }
  return errors;
};
