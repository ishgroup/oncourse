import {IAction} from "../../../actions/IshAction";
import {ThemesState} from "./State";
import {
  DELETE_THEME_FULFILLED,
  GET_THEMES_FULFILLED, SAVE_THEME_FULFILLED, UPDATE_THEME_STATE,
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
        items: state.items.map(item => item.id === id ? {...item, ...props} : item),
      };
    }

    case UPDATE_THEME_STATE: {
      const theme = action.payload;

      return {
        ...state,
        items: state.items.map(item => item.id === theme.id ? {...item, ...theme} : item),
      };
    }

    case DELETE_THEME_FULFILLED: {
      const id = action.payload;
      const index = state.items.findIndex(item => item.id === id);
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
