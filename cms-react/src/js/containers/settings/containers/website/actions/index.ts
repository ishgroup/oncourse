import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_WEBSITE_SETTINGS_REQUEST = _toRequestType("settings/get/checkout");
export const GET_WEBSITE_SETTINGS_FULFILLED = FULFILLED(GET_WEBSITE_SETTINGS_REQUEST);

export const SET_WEBSITE_SETTINGS_REQUEST = _toRequestType("settings/set/checkout");
export const SET_WEBSITE_SETTINGS_FULFILLED = FULFILLED(SET_WEBSITE_SETTINGS_REQUEST);

export const getWebsiteSettings = () => ({
  type: GET_WEBSITE_SETTINGS_REQUEST,
});
export const setWebsiteSettings = settings => ({
  type: GET_WEBSITE_SETTINGS_REQUEST,
  payload: settings,
});
