import {_toRequestType, FULFILLED} from "../common/actions/ActionUtils";

export const LOG_OUT_REQUEST: string = _toRequestType('user/logout');
export const LOG_OUT_FULFILLED: string = FULFILLED(LOG_OUT_REQUEST);


export const logout = () => ({
  type: LOG_OUT_REQUEST,
});
