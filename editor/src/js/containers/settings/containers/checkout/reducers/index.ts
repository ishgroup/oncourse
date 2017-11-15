import {IAction} from "../../../../../actions/IshAction";
import {CheckoutSettingsState} from "./State";
import {
  GET_CHECKOUT_SETTINGS_FULFILLED, GET_CHECKOUT_SETTINGS_REQUEST, SET_CHECKOUT_SETTINGS_FULFILLED,
  SET_CHECKOUT_SETTINGS_REQUEST,
} from "../actions";
import {UNHANDLED_ERROR} from "../../../../../common/actions";

export const checkoutSettingsReducer = (state: CheckoutSettingsState = new CheckoutSettingsState(), action: IAction<any>): CheckoutSettingsState => {
  switch (action.type) {

    case GET_CHECKOUT_SETTINGS_REQUEST:
    case SET_CHECKOUT_SETTINGS_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case UNHANDLED_ERROR:
      return {
        ...state,
        fetching: false,
      };

    case GET_CHECKOUT_SETTINGS_FULFILLED:
    case SET_CHECKOUT_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload,
        fetching: false,
      };

    default:
      return state;
  }
};
