import {Actions} from "../actions/Actions";

export const DEFAULT_ENROL_PATH = "/enrol/";

/**
 * Handle changing enrolPath property.
 */
export const EnrolPathReducer = (state = DEFAULT_ENROL_PATH, action): any => {
  switch (action.type) {
    case Actions.EnrolPathUpdate:
      return action.payload;
    default:
      return state;
  }
};

