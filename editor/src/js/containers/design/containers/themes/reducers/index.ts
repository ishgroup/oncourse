import {IAction} from "../../../../../actions/IshAction";
import {ThemesState} from "./State";
import {
  ADD_THEME_FULFILLED,
  ADD_THEME_REQUEST,
  DELETE_THEME_FULFILLED, DELETE_THEME_REQUEST,
  GET_THEMES_FULFILLED, GET_THEMES_REQUEST, SAVE_THEME_FULFILLED, SAVE_THEME_REQUEST, UPDATE_THEME_STATE,
} from "../actions";

export const themesReducer = (state: ThemesState = new ThemesState(), action: IAction<any>): ThemesState => {
  switch (action.type) {

    case GET_THEMES_REQUEST:
    case SAVE_THEME_REQUEST:
    case ADD_THEME_REQUEST:
    case DELETE_THEME_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case GET_THEMES_FULFILLED:
      return {
        ...state,
        items: action.payload,
        fetching: false,
      };

    case SAVE_THEME_FULFILLED: {
      const {id, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(item => item.id === id ? {...item, ...props} : item),
        fetching: false,
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
        fetching: false,
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
      const id = action.payload;
      const index = state.items.findIndex(item => item.id === id);
      const newThemes = state.items;

      if (index !== -1) {
        newThemes.splice(index, 1);
      }

      return {
        ...state,
        items: newThemes,
        fetching: false,
      };
    }

    default:
      return state;
  }
};
