export const validateForbiddenSymbols = value => (value.match(/[\\"#]/g) ? "Forbidden symbols found" : undefined);
