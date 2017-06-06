import {Actions} from "../actions/Actions";
import {DEFAULT_ENROL_PATH} from "../services/HTMLMarker";



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

