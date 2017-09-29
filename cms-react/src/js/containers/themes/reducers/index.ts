import {IAction} from "../../../actions/IshAction";
import {ThemesState} from "./State";
import {
  DELETE_THEME_FULFILLED,
  GET_THEMES_FULFILLED, SAVE_THEME_FULFILLED,
} from "../actions";

export const themesReducers = (state: ThemesState = new ThemesState(), action: IAction<any>): ThemesState => {
  switch (action.type) {

    case GET_THEMES_FULFILLED:
      return {
        ...state,
        items: action.payload,
      };

    case SAVE_THEME_FULFILLED: {
      const {id, ...props} = action.payload;

      return {
        ...state,
        items: state.items.map(page => page.id === id ? {...page, ...props} : page),
      };
    }

    case DELETE_THEME_FULFILLED: {
      const id = action.payload;
      const index = state.items.findIndex(page => page.id === id);
      const newThemes = state.items;

      if (index !== -1) {
        newThemes.splice(index, 1);
      }

      return {
        ...state,
        items: newThemes,
      };
    }

    default:
      return state;
  }
};
