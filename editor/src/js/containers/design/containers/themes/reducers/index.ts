import {IAction} from "../../../../../actions/IshAction";
import {ThemesState} from "./State";
import {
  ADD_THEME_FULFILLED, DELETE_THEME_FULFILLED, GET_LAYOUTS_FULFILLED,
  GET_THEMES_FULFILLED, SAVE_THEME_FULFILLED, UPDATE_THEME_STATE,
} from "../actions";

export const themesReducer = (state: ThemesState = new ThemesState(), action: IAction<any>): ThemesState => {
  switch (action.type) {

    case GET_THEMES_FULFILLED:
      return {
        ...state,
        items: action.payload,
      };

    case GET_LAYOUTS_FULFILLED:
      return {
        ...state,
        layouts: action.payload,
      };

    case SAVE_THEME_FULFILLED: {
      const {id, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(item => item.id === id ? {...item, ...props} : item),
      };

      if (!state.items.find(theme => theme.id === id)) {
        ns.items.push({id, ...props});
      }

      return ns;
    }

    case ADD_THEME_FULFILLED: {
      const theme = action.payload;

      return {
        ...state,
        items: state.items.concat(theme),
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
      const title = action.payload;
      const index = state.items.findIndex(item => item.title === title);
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
