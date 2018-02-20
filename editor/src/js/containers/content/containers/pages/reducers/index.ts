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
      const {serialNumber, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(page => page.serialNumber === serialNumber ? {...page, ...props} : page),
      };

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
      const pageId = action.payload;
      const index = state.items.findIndex(page => page.id === pageId);
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
      const {html, serialNumber} = action.payload;

      return {
        ...state,
        items: state.items.map(item => item.serialNumber === serialNumber ? {...item, renderHtml: html} : item),
      };
    }

    case CLEAR_RENDER_HTML: {
      const serialNumber = action.payload;

      return {
        ...state,
        items: state.items.map(item => item.serialNumber === serialNumber ? {...item, renderHtml: ''} : item),
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
