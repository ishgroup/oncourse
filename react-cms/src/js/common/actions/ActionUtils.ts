import * as Lodash from "lodash";

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

/**
 * Convert Redux Action type  to  Epic Action Request type
 */
export const _toRequestType = (type: string): string => {
  return `${type}/request`;
};

/**
 * Convert Redux Action to Redux Action Reject type
 */
export const _toRejectType = (type: string): string => {
  return `${type}/reject`;
};


export const _epicToRedux = (action: any, type: string, payload: any): any => {
  const result = Lodash.clone(action);
  result.payload = payload;
  result.type = payload;
  return result;
};
