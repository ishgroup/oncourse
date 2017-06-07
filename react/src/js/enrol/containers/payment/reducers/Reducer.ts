import {State} from "./State";
import {RESET_PAYMENT_STATE, UPDATE_PAYMENT_STATUS} from "../actions/Actions";
import {RESET_CHECKOUT_STATE} from "../../../actions/Actions";

export const Reducer = (state: State = new State(), action: { type: string, payload: PaymentResponse }): State => {
  switch (action.type) {
    case UPDATE_PAYMENT_STATUS:
      const ns = new State();
      ns.value = action.payload;
      return ns;
    case RESET_PAYMENT_STATE:
    case RESET_CHECKOUT_STATE:
      return new State();
    default:
      return state;
  }
};
