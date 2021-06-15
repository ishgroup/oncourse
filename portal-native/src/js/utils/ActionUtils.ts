export function postfix(actionType: string, name: string) {
  return `${actionType}_${name}`;
}

export function FULFILLED(actionType: string) {
  return postfix(actionType, 'FULFILLED');
}

/**
 * @Deprecated - use _toRequestType
 */
export function REJECTED(actionType: string) {
  return postfix(actionType, 'REJECTED');
}

/**
 * Convert Redux Action type  to  Epic Action Request type
 */
export const toRequestType = (type: string): string => `${type}/request`;
