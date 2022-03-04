export const validateAqlFilterOrTagName = value => (value.match(/[^\w_ -]+/g) ? "Forbidden symbol" : undefined);
