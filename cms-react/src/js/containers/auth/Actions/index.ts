import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const SUBMIT_LOGIN_FORM_REQUEST: string = _toRequestType('login/submit');
export const SUBMIT_LOGIN_FORM_FULFILLED: string = FULFILLED(SUBMIT_LOGIN_FORM_REQUEST);

export const submitLoginForm = form => ({
  type: SUBMIT_LOGIN_FORM_REQUEST,
  payload: form,
});
