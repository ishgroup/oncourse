import {IAction} from "../../../../../actions/IshAction";
import {WebsiteSettingsState} from "./State";
import {
  GET_WEBSITE_SETTINGS_FULFILLED,
} from "../actions";

export const websiteSettingsReducer = (state: WebsiteSettingsState = new WebsiteSettingsState(), action: IAction<any>): WebsiteSettingsState => {
  switch (action.type) {

    case GET_WEBSITE_SETTINGS_FULFILLED:
      return {
        ...state,
      };

    default:
      return state;
  }
};
