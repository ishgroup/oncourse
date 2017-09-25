import {IAction} from "../../../actions/IshAction";
import {PagesState} from "./State";
import {
  DELETE_PAGE_FULFILLED,
  GET_PAGES_FULFILLED, SAVE_PAGE_FULFILLED,
} from "../actions";

export const pageReducer = (state: PagesState = new PagesState(), action: IAction<any>): PagesState => {
  switch (action.type) {

    case GET_PAGES_FULFILLED:
      return {
        ...state,
        pages: action.payload,
      };

    case SAVE_PAGE_FULFILLED: {
      const {id, ...props} = action.payload;

      return {
        ...state,
        pages: state.pages.map(page => page.id === id ? {...page, ...props} : page),
      };
    }

    case DELETE_PAGE_FULFILLED: {
      const id = action.payload;
      const index = state.pages.findIndex(page => page.id === id);
      const newPages = state.pages;

      if (index !== -1) {
        newPages.splice(index, 1);
      }

      return {
        ...state,
        pages: newPages,
      };
    }

    default:
      return state;
  }
};
