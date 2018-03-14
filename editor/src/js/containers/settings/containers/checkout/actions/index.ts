import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_CHECKOUT_SETTINGS_REQUEST = _toRequestType("settings/get/checkout");
export const GET_CHECKOUT_SETTINGS_FULFILLED = FULFILLED(GET_CHECKOUT_SETTINGS_REQUEST);

export const SET_CHECKOUT_SETTINGS_REQUEST = _toRequestType("settings/set/checkout");
export const SET_CHECKOUT_SETTINGS_FULFILLED = FULFILLED(SET_CHECKOUT_SETTINGS_REQUEST);

export const getCheckoutSettings = () => ({
  type: GET_CHECKOUT_SETTINGS_REQUEST,
});
export const setCheckoutSettings = settings => ({
  type: SET_CHECKOUT_SETTINGS_REQUEST,
  payload: settings,
});
