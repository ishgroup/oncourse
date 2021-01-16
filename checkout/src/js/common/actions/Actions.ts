import {_toRequestType, FULFILLED} from "./ActionUtils";

export const Actions = {
  UPDATE_WILLOW_CONFIG: "common/update/config",
  UPDATE_PAYMENT_SUCCESS_URL: "common/update/paymentSuccessUrl",

  GET_PREFERENCES_REQUEST: _toRequestType("common/update/preferences"),
  GET_PREFERENCES_REQUEST_FULFILLED: FULFILLED(_toRequestType("common/update/preferences")),
};

export const getPreferences = () => ({
  type: Actions.GET_PREFERENCES_REQUEST,
});

export const updatePaymentSuccessUrl = (url: string) => ({
  type: Actions.UPDATE_PAYMENT_SUCCESS_URL,
  payload: url
});
