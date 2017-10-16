import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_WEBSITE_SETTINGS_REQUEST = _toRequestType("settings/get/website");
export const GET_WEBSITE_SETTINGS_FULFILLED = FULFILLED(GET_WEBSITE_SETTINGS_REQUEST);

export const SET_WEBSITE_SETTINGS_REQUEST = _toRequestType("settings/set/website");
export const SET_WEBSITE_SETTINGS_FULFILLED = FULFILLED(SET_WEBSITE_SETTINGS_REQUEST);

export const getWebsiteSettings = () => ({
  type: GET_WEBSITE_SETTINGS_REQUEST,
});
export const setWebsiteSettings = settings => ({
  type: SET_WEBSITE_SETTINGS_REQUEST,
  payload: settings,
});
