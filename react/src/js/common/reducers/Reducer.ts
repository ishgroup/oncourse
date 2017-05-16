import {Actions} from "../actions/Actions";

export const DEFAULT_ENROL_PATH = "/checkout/";

/**
 * Handle changing checkoutPath property.
 */
export const CheckoutPathReducer = (state = DEFAULT_ENROL_PATH, action): any => {
  switch (action.type) {
    case Actions.CheckoutPathUpdate:
      return action.payload;
    default:
      return state;
  }
};

