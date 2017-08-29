import {Actions} from "../actions/Actions";

/**
 * Handle changing global preferences.
 */

export const configReducer = (state = {}, action): any => {
  switch (action.type) {
    case Actions.UPDATE_PREFERENCES_REQUEST_FULFILLED:
      return action.payload;
    default:
      return state;
  }
}
