import {State} from "./State";
import {UPDATE_PAYMENT_STATUS} from "../actions/Actions";

export const Reducer = (state: State = new State(), action: { type: string, payload: PaymentResponse }): State => {
  switch (action.type) {
    case UPDATE_PAYMENT_STATUS:
      const ns = new State();
      ns.value = action.payload;
      return ns;
    default:
      return state;
  }
};
