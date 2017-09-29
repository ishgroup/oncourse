import {IAction} from "../../../actions/IshAction";
import {ThemesState} from "./State";
import {
  GET_THEMES_FULFILLED,
} from "../actions";

export const themesReducers = (state: ThemesState = new ThemesState(), action: IAction<any>): ThemesState => {
  switch (action.type) {

    case GET_THEMES_FULFILLED:
      return {
        ...state,
        items: action.payload,
      };

    default:
      return state;
  }
};
