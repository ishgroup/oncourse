import {IshState} from "../../../../services/IshState";
import {UPDATE_AMOUNT} from "../../../actions/Actions";
import {Store} from "redux";
import {Phase} from "../../../reducers/State";
import {processPaymentV2} from "../actions/Actions";
import {Tabs} from "../reducers/State";

function createValidatePaymentEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      UPDATE_AMOUNT
    )
    .flatMap(() => {
      const state = store.getState();
      const isCCPayment = state.checkout.page === Phase.Payment
        && state.checkout.payment.currentTab === Tabs.creditCard;
      return isCCPayment ? [processPaymentV2(true)] : [];
    });
}

export const EpicValidatePayment = createValidatePaymentEpic();
