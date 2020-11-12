const MAX_PHONE_LENGTH = 20;
export const validatePhoneNumber = (value, allValues, props, name) =>
  value && value.length > MAX_PHONE_LENGTH ? `Phone number must be less than ${MAX_PHONE_LENGTH} symbols` : undefined;
