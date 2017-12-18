import {IAction} from "../../../../../actions/IshAction";
import {WebsiteSettingsState} from "./State";
import {
  GET_WEBSITE_SETTINGS_FULFILLED, SET_WEBSITE_SETTINGS_FULFILLED,
} from "../actions";

export const websiteSettingsReducer = (state: WebsiteSettingsState = new WebsiteSettingsState(), action: IAction<any>): WebsiteSettingsState => {
  switch (action.type) {

    case GET_WEBSITE_SETTINGS_FULFILLED:
    case SET_WEBSITE_SETTINGS_FULFILLED:
      return {
        ...state,
        ...action.payload,
        refreshSettings: true,
      };

    default:
      return {
        ...state,
        refreshSettings: false,
      };
  }
};
