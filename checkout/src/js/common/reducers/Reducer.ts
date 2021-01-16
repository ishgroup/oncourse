import {Actions} from "../actions/Actions";
import {WillowConfig} from "../../configLoader";

/**
 * Handle changing config properties.
 */

export const configReducer = (state: WillowConfig = new WillowConfig({}), action): any => {
  switch (action.type) {
    case Actions.UPDATE_WILLOW_CONFIG:
      return action.payload;
    case Actions.UPDATE_PAYMENT_SUCCESS_URL:
      return {
        ...state,
        paymentSuccessURL: action.payload
      }
    default:
      return state;
  }
}
