import {Actions} from "../actions/Actions";
import {DEFAULT_CHECKOUT_PATH} from "../services/HTMLMarker";


/**
 * Handle changing checkoutPath property.
 */
export const CheckoutPathReducer = (state = DEFAULT_CHECKOUT_PATH, action): any => {
  switch (action.type) {
    case Actions.CheckoutPathUpdate:
      return action.payload;
    default:
      return state;
  }
};

export const configReducer = (state = {}, action): any => {
  switch (action.type) {
    case Actions.UPDATE_WILLOW_CONFIG:
      return action.payload;
    default:
      return state;
  }
}
