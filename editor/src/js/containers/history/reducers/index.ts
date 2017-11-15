import {IAction} from "../../../actions/IshAction";
import {HistoryState} from "./State";
import {
  GET_VERSIONS_FULFILLED, GET_VERSIONS_REQUEST,
} from "../actions";
import {UNHANDLED_ERROR} from "../../../common/actions";

export const historyReducer = (state: HistoryState = new HistoryState(), action: IAction<any>): HistoryState => {
  switch (action.type) {

    case GET_VERSIONS_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case UNHANDLED_ERROR:
      return {
        ...state,
        fetching: false,
      };

    case GET_VERSIONS_FULFILLED:
      return {
        ...state,
        versions: action.payload,
        fetching: false,
      };

    default:
      return state;
  }
};
