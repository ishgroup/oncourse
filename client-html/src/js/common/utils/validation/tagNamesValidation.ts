export const validateAqlFilterOrTagName = value => (value.match(/[\\"#]/g) ? "Forbidden symbol" : undefined);
