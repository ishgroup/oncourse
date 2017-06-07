import * as SummaryActions from "../actions/Actions";
import {ContactNodeToState, merge, State} from "./State";
import {RESET_CHECKOUT_STATE} from "../../../actions/Actions";

export const Reducer = (state: State = ContactNodeToState([]), action: { type: string, payload: State }): State => {
  switch (action.type) {
    case SummaryActions.SELECT_ITEM:
      return merge(state, action.payload);
    case SummaryActions.ItemsLoad:
      return action.payload;
    case RESET_CHECKOUT_STATE:
      return ContactNodeToState([]);
    default:
      return state;
  }
};
