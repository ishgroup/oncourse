import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_REDIRECT_SETTINGS_REQUEST = _toRequestType("settings/get/redirect");
export const GET_REDIRECT_SETTINGS_FULFILLED = FULFILLED(GET_REDIRECT_SETTINGS_REQUEST);

export const SET_REDIRECT_SETTINGS_REQUEST = _toRequestType("settings/set/redirect");
export const SET_REDIRECT_SETTINGS_FULFILLED = FULFILLED(SET_REDIRECT_SETTINGS_REQUEST);

export const getRedirectSettings = () => ({
  type: GET_REDIRECT_SETTINGS_REQUEST,
});
export const setRedirectSettings = settings => ({
  type: SET_REDIRECT_SETTINGS_REQUEST,
  payload: settings,
});
