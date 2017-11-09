import {IAction} from "../../../../../actions/IshAction";
import {WebsiteSettingsState} from "./State";
import {
  GET_WEBSITE_SETTINGS_FULFILLED, GET_WEBSITE_SETTINGS_REQUEST, SET_WEBSITE_SETTINGS_FULFILLED,
  SET_WEBSITE_SETTINGS_REQUEST,
} from "../actions";

export const websiteSettingsReducer = (state: WebsiteSettingsState = new WebsiteSettingsState(), action: IAction<any>): WebsiteSettingsState => {
  switch (action.type) {

    case GET_WEBSITE_SETTINGS_REQUEST:
    case SET_WEBSITE_SETTINGS_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case GET_WEBSITE_SETTINGS_FULFILLED:
    case SET_WEBSITE_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload,
        fetching: false,
      };

    default:
      return state;
  }
};
