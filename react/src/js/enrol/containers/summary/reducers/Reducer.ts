import * as SummaryActions from "../actions/Actions";
import {ContactNodeToState, merge, State} from "./State";

export const Reducer = (state: State = ContactNodeToState([]), action: { type: string, payload: State }): State => {
  switch (action.type) {
    case SummaryActions.SELECT_ITEM:
      return merge(state, action.payload);
    case SummaryActions.ItemsLoad:
      return action.payload;
    default:
      return state;
  }
};
