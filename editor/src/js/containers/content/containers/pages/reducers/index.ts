import {IAction} from "../../../../../actions/IshAction";
import {PagesState} from "./State";
import {
  ADD_PAGE_FULFILLED, CLEAR_RENDER_HTML,
  DELETE_PAGE_FULFILLED, GET_PAGE_RENDER_FULFILLED,
  GET_PAGES_FULFILLED, SAVE_PAGE_FULFILLED, TOGGLE_EDIT_MODE,
} from "../actions";

export const pageReducer = (state: PagesState = new PagesState(), action: IAction<any>): PagesState => {
  switch (action.type) {

    case GET_PAGES_FULFILLED:
      return {
        ...state,
        items: action.payload,
      };

    case SAVE_PAGE_FULFILLED: {
      const {number, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(page => page.number === number ? {...page, ...props} : page),
      };

      if (!state.items.find(page => page.number === number)) {
        ns.items.push({number, ...props});
      }

      return ns;
    }

    case ADD_PAGE_FULFILLED: {
      const page = action.payload;

      return {
        ...state,
        items: state.items.concat(page),
      };
    }

    case DELETE_PAGE_FULFILLED: {
      const pageNumber = action.payload;
      const index = state.items.findIndex(page => page.number === pageNumber);
      const newPages = state.items;

      if (index !== -1) {
        newPages.splice(index, 1);
      }

      return {
        ...state,
        items: newPages,
      };
    }

    case GET_PAGE_RENDER_FULFILLED: {
      const {html, pageNumber} = action.payload;

      return {
        ...state,
        items: state.items.map(item => item.number === pageNumber ? {...item, renderHtml: html} : item),
      };
    }

    case CLEAR_RENDER_HTML: {
      const pageNumber = action.payload;

      return {
        ...state,
        items: state.items.map(item => item.number === pageNumber ? {...item, renderHtml: ''} : item),
      };
    }

    case TOGGLE_EDIT_MODE: {
      const flag = action.payload;

      return {
        ...state,
        editMode: flag,
      };
    }

    default:
      return state;
  }
};
