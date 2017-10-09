import {IAction} from "../../../actions/IshAction";
import {HistoryState} from "./State";
import {
  GET_VERSIONS_FULFILLED,
} from "../actions";

export const historyReducer = (state: HistoryState = new HistoryState(), action: IAction<any>): HistoryState => {
  switch (action.type) {

    case GET_VERSIONS_FULFILLED:
      return {
        ...state,
        versions: action.payload,
      };

    default:
      return state;
  }
};
