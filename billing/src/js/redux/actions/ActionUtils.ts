export function FULFILLED(actionType: string) {
  return postfix(actionType, "FULFILLED");
}

/**
 * @Deprecated - use _toRequestType
 */
export function REJECTED(actionType: string) {
  return postfix(actionType, "REJECTED");
}

export function postfix(actionType: string, name: string) {
  return `${actionType}_${name}`;
}
