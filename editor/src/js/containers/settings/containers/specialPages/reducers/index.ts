import {IAction} from "../../../../../actions/IshAction";
import {SpecialPageSettingsState} from "./State";
import {
  GET_SPECIAL_PAGE_SETTINGS_FULFILLED, SET_SPECIAL_PAGE_SETTINGS_FULFILLED,
} from "../actions";

export const specialPagesReducer = (state: SpecialPageSettingsState = new SpecialPageSettingsState(), action: IAction<any>): SpecialPageSettingsState => {
  switch (action.type) {

    case GET_SPECIAL_PAGE_SETTINGS_FULFILLED:
    case SET_SPECIAL_PAGE_SETTINGS_FULFILLED:
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
