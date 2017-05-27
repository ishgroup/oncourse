import * as SummaryActions from "../actions/Actions";
import {convert, merge, State} from "./State";

export const Reducer = (state: State = convert([]), action: { type: string, payload: State }): State => {
  switch (action.type) {
    case SummaryActions.ItemSelect:
      return merge(state, action.payload);
    case SummaryActions.ItemsLoad:
      return action.payload;
    default:
      return state;
  }
};
