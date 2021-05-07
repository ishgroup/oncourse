import {IAction} from "../../../../actions/IshAction";
import {NavigationState} from "./State";
import {SHOW_NAVIGATION, HIDE_NAVIGATION, SET_ACTIVE_URL} from "../actions";

export const navigationReducer = (state: NavigationState = new NavigationState(), action: IAction<any>): NavigationState => {
  switch (action.type) {
    case SHOW_NAVIGATION:
      return {
        ...state,
        showNavigation: true,
      };
    case HIDE_NAVIGATION:
      return {
        ...state,
        showNavigation: false,
      };
    case SET_ACTIVE_URL:
      return {
        ...state,
        activeUrl: action.payload,
      }
    default:
      return {
        ...state,
      };
  }
};
