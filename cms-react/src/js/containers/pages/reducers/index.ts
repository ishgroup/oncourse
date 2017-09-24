import {IAction} from "../../../actions/IshAction";
import {PagesState} from "./State";
import {
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

    default:
      return state;
  }
};
