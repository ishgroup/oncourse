import {IAction} from "../../../../../actions/IshAction";
import {PagesState} from "./State";
import {
  ADD_PAGE_FULFILLED, ADD_PAGE_REQUEST, CLEAR_RENDER_HTML,
  DELETE_PAGE_FULFILLED, DELETE_PAGE_REQUEST, GET_PAGE_RENDER_FULFILLED, GET_PAGE_RENDER_REQUEST,
  GET_PAGES_FULFILLED, GET_PAGES_REQUEST, SAVE_PAGE_FULFILLED, SAVE_PAGE_REQUEST, TOGGLE_EDIT_MODE,
} from "../actions";

export const pageReducer = (state: PagesState = new PagesState(), action: IAction<any>): PagesState => {
  switch (action.type) {

    case GET_PAGES_FULFILLED:
      return {
        ...state,
        items: action.payload,
        fetching: false,
      };

    case SAVE_PAGE_REQUEST:
    case GET_PAGES_REQUEST:
    case DELETE_PAGE_REQUEST:
    case GET_PAGE_RENDER_REQUEST:
    case ADD_PAGE_REQUEST: {
      return {...state, fetching: true};
    }

    case SAVE_PAGE_FULFILLED: {
      const {id, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(page => page.id === id ? {...page, ...props} : page),
        fetching: false,
      };

      if (!state.items.find(page => page.id === id)) {
        ns.items.push({id, ...props});
      }

      return ns;
    }

    case ADD_PAGE_FULFILLED: {
      const page = action.payload;

      return {
        ...state,
        fetching: false,
        items: state.items.concat(page),
      };
    }

    case DELETE_PAGE_FULFILLED: {
      const id = action.payload;
      const index = state.items.findIndex(page => page.id === id);
      const newPages = state.items;

      if (index !== -1) {
        newPages.splice(index, 1);
      }

      return {
        ...state,
        fetching: false,
        items: newPages,
      };
    }

    case GET_PAGE_RENDER_FULFILLED: {
      const {html, id} = action.payload;

      return {
        ...state,
        fetching: false,
        items: state.items.map(item => item.id === id ? {...item, renderHtml: html} : item),
      };
    }

    case CLEAR_RENDER_HTML: {
      const id = action.payload;

      return {
        ...state,
        items: state.items.map(item => item.id === id ? {...item, renderHtml: ''} : item),
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
