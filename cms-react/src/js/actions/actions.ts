import {_toRequestType, FULFILLED} from "../common/actions/ActionUtils";

export const LOG_OUT_REQUEST: string = _toRequestType('user/logout');
export const LOG_OUT_FULFILLED: string = FULFILLED('user/logout');


export const logout = () => ({
  type: LOG_OUT_REQUEST,
});
