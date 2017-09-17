import {IAction} from "../../../actions/IshAction";
import {PagesState} from "./State";
import {GET_PAGES_REQUEST, GET_PAGES_FULFILLED} from "../actions";

export const pageReducer = (state: PagesState = new PagesState(), action: IAction<any>): PagesState => {
  switch (action.type) {

    case GET_PAGES_FULFILLED:
      return {
        ...state,
        pages: action.payload,
      };

    default:
      return state;
  }
};
