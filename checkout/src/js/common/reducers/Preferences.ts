import {Actions} from "../actions/Actions";
import {Preferences} from "../../model";
import {IAction} from "../../actions/IshAction";

/**
 * Handle changing global preferences.
 */

export const preferencesReducer = (state: Preferences = {}, action: IAction<Preferences>): Preferences => {
  switch (action.type) {

    case Actions.GET_PREFERENCES_REQUEST_FULFILLED:
      return {
        ...state,
        ...action.payload,
      };

    default:
      return state;
  }
};