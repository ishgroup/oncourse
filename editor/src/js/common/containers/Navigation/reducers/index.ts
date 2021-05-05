import {IAction} from "../../../../actions/IshAction";
import {NavigationState} from "./State";
import {SHOW_NAVIGATION, HIDE_NAVIGATION} from "../actions";

export const navigationReducer = (state: NavigationState = new NavigationState(), action: IAction<any>): NavigationState => {
  switch (action.type) {
    case SHOW_NAVIGATION:
      return {
        ...state,
        showNavigation: true,
      };
    case HIDE_NAVIGATION:
      return {
        ...new NavigationState(),
        showNavigation: false,
      };
    default:
      return {
        ...state,
        showNavigation: false,
      };
  }
};
