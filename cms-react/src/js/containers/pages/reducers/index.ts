import {IAction} from "../../../actions/IshAction";
import {PagesState} from "./State";
import {GET_PAGES_FULFILLED, SAVE_PAGE_HTML_FULFILLED, SAVE_PAGE_SETTINGS_FULFILLED} from "../actions";

export const pageReducer = (state: PagesState = new PagesState(), action: IAction<any>): PagesState => {
  switch (action.type) {

    case GET_PAGES_FULFILLED:
      return {
        ...state,
        pages: action.payload,
      };

    case SAVE_PAGE_SETTINGS_FULFILLED: {
      const {id, ...prop} = action.payload;

      return {
        ...state,
        pages: state.pages.map(page => page.id === id ? {...page, ...prop} : page),
      };
    }

    case SAVE_PAGE_HTML_FULFILLED: {
      const {id, html} = action.payload;

      return {
        ...state,
        pages: state.pages.map(page => page.id === id ? {...page, html} : page),
      };
    }

    default:
      return state;
  }
};
