import {IAction} from "../../../../../actions/IshAction";
import {RedirectSettingsState} from "./State";
import {
  GET_REDIRECT_SETTINGS_FULFILLED, GET_REDIRECT_SETTINGS_REQUEST, SET_REDIRECT_SETTINGS_FULFILLED,
  SET_REDIRECT_SETTINGS_REQUEST,
} from "../actions";
import {UNHANDLED_ERROR} from "../../../../../common/actions";

export const redirectSettingsReducer = (state: RedirectSettingsState = new RedirectSettingsState(), action: IAction<any>): RedirectSettingsState => {
  switch (action.type) {

    case GET_REDIRECT_SETTINGS_REQUEST:
    case SET_REDIRECT_SETTINGS_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case UNHANDLED_ERROR:
      return {
        ...state,
        fetching: false,
      };

    case GET_REDIRECT_SETTINGS_FULFILLED:
    case SET_REDIRECT_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload,
        fetching: false,
      };

    default:
      return state;
  }
};
