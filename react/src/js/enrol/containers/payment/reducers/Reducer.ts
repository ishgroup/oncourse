import {State} from "./State";
import {RESET_PAYMENT_STATE, UPDATE_PAYMENT_STATUS} from "../actions/Actions";
import {IAction} from "../../../../actions/IshAction";

export const Reducer = (state: State = new State(), action: IAction<any>): State => {
  switch (action.type) {
    case UPDATE_PAYMENT_STATUS:
      const ns = new State();
      ns.value = action.payload;
      return ns;
    case RESET_PAYMENT_STATE:
      return new State();
    default:
      return state;
  }
};
