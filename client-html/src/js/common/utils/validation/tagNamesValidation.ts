export const validateTagName = value => (value.match(/[@#_,"]/g) ? "Forbidden symbol" : undefined);
