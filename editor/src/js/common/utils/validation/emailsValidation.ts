export const validateEmail = value => {
  if (!value) {
    return undefined;
  }

  const errorMessage = "Please enter valid email address";

  if (!value.includes("@")) {
    return errorMessage;
  }
  const splitted = value.split("@");
  return /\../.test(splitted[splitted.length - 1]) ? undefined : errorMessage;
};
