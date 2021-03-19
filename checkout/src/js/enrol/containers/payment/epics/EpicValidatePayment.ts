import {IshState} from "../../../../services/IshState";
import {UPDATE_AMOUNT} from "../../../actions/Actions";
import {Store} from "redux";
import {Phase} from "../../../reducers/State";
import {processPaymentV2} from "../actions/Actions";

function createValidatePaymentEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      UPDATE_AMOUNT
    )
    .flatMap(() => {
      const isPayment = store.getState().checkout.page === Phase.Payment;
      return isPayment ? [processPaymentV2(true)] : [];
    });
}

export const EpicValidatePayment = createValidatePaymentEpic();
