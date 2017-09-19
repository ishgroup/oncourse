import {IAction} from "../../../actions/IshAction";
import {PagesState} from "./State";
import {GET_PAGES_REQUEST, GET_PAGES_FULFILLED, EDIT_PAGE_SETTINGS} from "../actions";

export const pageReducer = (state: PagesState = new PagesState(), action: IAction<any>): PagesState => {
  switch (action.type) {

    case GET_PAGES_FULFILLED:
      return {
        ...state,
        pages: action.payload,
      };

    case EDIT_PAGE_SETTINGS:
      const {id, ...prop} = action.payload;

      return {
        ...state,
        pages: state.pages.map(page => page.id === id ? {...page, ...prop} : page),
      };

    default:
      return state;
  }
};
