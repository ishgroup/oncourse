import { IAction } from '../../../../../actions/IshAction';
import {
  GET_CHECKOUT_SETTINGS_FULFILLED, SET_CHECKOUT_SETTINGS_FULFILLED,
} from '../actions';
import { CheckoutSettings } from '../../../../../../../build/generated-sources/api';

const defaultState: CheckoutSettings = {
  allowCreateContact: null,
  contactAgeWhenNeedParent: 18,
  termsLabel: null,
  termsUrl: null,
};

export const checkoutSettingsReducer = (state: CheckoutSettings = defaultState, action: IAction<any>): CheckoutSettings => {
  switch (action.type) {
    case SET_CHECKOUT_SETTINGS_FULFILLED:
    case GET_CHECKOUT_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload
      };

    default:
      return {
        ...state
      };
  }
};
