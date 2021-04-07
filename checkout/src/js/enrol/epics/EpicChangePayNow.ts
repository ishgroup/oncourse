import * as EpicUtils from "../../common/epics/EpicUtils";
import {CheckoutModel} from "../../model";
import {IshState} from "../../services/IshState";
import {UPDATE_PAYNOW, updateAmount} from "../actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {Epic} from "redux-observable";
import {changeTab} from "../containers/payment/actions/Actions";
import {Tabs} from "../containers/payment/reducers/State";

const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: UPDATE_PAYNOW,
  getData: (payload, state: IshState) => CheckoutService.getUpdateModel(state, payload.val),
  processData: (model: CheckoutModel, state: IshState) => {
    return ProcessCheckoutModel.process(model, state);
  },
};

export const EpicChangePayNow: Epic<any, any> = EpicUtils.Create(request);

export class ProcessCheckoutModel {
  static process = (model: CheckoutModel, state: IshState): any[] => {
    const tab = state.checkout.payment.currentTab;

    const creditCardAvailable = state.preferences.hasOwnProperty('creditCardEnabled')
      ? state.preferences.creditCardEnabled
      : true;

    const result = [];

    if (model.error) {
      return [
        updateAmount(model.amount),
      ];
    }

    result.push(updateAmount(model.amount));

        // change tab to pay later for zero payments
    if (Number(model.amount.ccPayment) === 0 && tab !== Tabs.payLater) {
      result.push(changeTab(Tabs.payLater));
    }

        // change tab to credit card for not zero payment if credit card method enabled
    if (Number(model.amount.ccPayment) !== 0 && tab === Tabs.payLater && creditCardAvailable) {
      result.push(changeTab(Tabs.creditCard));
    }

    return result;
  }
}
