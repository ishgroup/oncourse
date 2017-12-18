import {IAction} from "../../../../../actions/IshAction";
import {CheckoutSettingsState} from "./State";
import {
  GET_CHECKOUT_SETTINGS_FULFILLED, SET_CHECKOUT_SETTINGS_FULFILLED,
} from "../actions";

export const checkoutSettingsReducer = (state: CheckoutSettingsState = new CheckoutSettingsState(), action: IAction<any>): CheckoutSettingsState => {
  switch (action.type) {

    case GET_CHECKOUT_SETTINGS_FULFILLED:
    case SET_CHECKOUT_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload,
        refreshSettings: true,
      };

    default:
      return {
        ...state,
        refreshSettings: false,
      };
  }
};
