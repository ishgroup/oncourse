import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const SUBMIT_LOGIN_FORM_REQUEST: string = _toRequestType('login/submit');
export const GET_USER_REQUEST: string = _toRequestType('login/getUser');

export const SUBMIT_LOGIN_FORM_FULFILLED: string = FULFILLED(SUBMIT_LOGIN_FORM_REQUEST);
export const GET_USER_FULFILLED: string = FULFILLED(GET_USER_REQUEST);

export const LOG_OUT_REQUEST: string = _toRequestType('user/logout');
export const LOG_OUT_FULFILLED: string = FULFILLED(LOG_OUT_REQUEST);

export const PAGE_RELOAD: string = "page/reload";

export const submitLoginForm = form => ({
  type: SUBMIT_LOGIN_FORM_REQUEST,
  payload: form,
});

export const getUser = () => ({
  type: GET_USER_REQUEST,
});

export const logout = () => ({
  type: LOG_OUT_REQUEST,
});

export const pageReload = () => ({
  type: PAGE_RELOAD,
});
