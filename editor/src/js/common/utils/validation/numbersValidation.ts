export const greaterThanNullValidation = value => {
  return typeof value === "number" && value <= 0 ? "Value must be greater than 0" : undefined;
};

export const greaterThanZeroIncludeValidation = value => {
  return typeof value === "number" && value < 0 ? "Value can't be less than 0" : undefined;
};

export const validateRangeInclusive = (value: number, min: number, max: number, message: string = undefined) => {
  return value >= min && value <= max ? undefined : message ? message : `Wrong value`;
};

export const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);
