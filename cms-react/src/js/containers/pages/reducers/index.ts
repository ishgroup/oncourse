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
        items: action.payload,
      };

    case SAVE_PAGE_FULFILLED: {
      const {id, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(page => page.id === id ? {...page, ...props} : page),
      };

      if (!state.items.find(page => page.id === id)) {
        ns.items.push({id, ...props});
      }

      return ns;
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
        items: newPages,
      };
    }

    default:
      return state;
  }
};
