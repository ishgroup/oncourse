import {Epic, ActionsObservable} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {IAction} from "../../actions/IshAction";
import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {UPDATE_PAYNOW, updateAmount} from "../actions/Actions";
import {showCommonError} from "../../common/epics/EpicUtils";
import {Tabs} from "../containers/payment/reducers/State";
import {changeTab} from "../containers/payment/actions/Actions";

export const EpicUpdatePayNow: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  let currentValidValue = 0;

  return action$.ofType(UPDATE_PAYNOW).flatMap((action: IAction<any>): any => {
    const state = store.getState();
    const amount = state.checkout.amount;
    const tab = state.checkout.payment.currentTab;
    const creditCardAvailable = state.preferences.hasOwnProperty('creditCardEnabled') ? state.preferences.creditCardEnabled : true;
    const newAmount = {...amount, payNow: action.payload.val};
    const errors = action.payload.validate ? CheckoutService.validatePayNow(newAmount) : [];
    const result = [];

    currentValidValue = !CheckoutService.validatePayNow(newAmount).length && action.payload.validate
      ? action.payload.val
      : currentValidValue || amount.minPayNow;

    if (errors.length) {
      return [
        updateAmount({...amount, payNow: currentValidValue}),
        showCommonError({message: errors[0]}),
      ];
    }

    result.push(updateAmount(newAmount));

    // change tab to pay later for zero payments
    if (Number(action.payload.val) === 0 && tab !== Tabs.payLater) {
      result.push(changeTab(Tabs.payLater));
    }

    // change tab to credit card for not zero payment if credit card method enabled
    if (Number(action.payload.val) !== 0 && tab === Tabs.payLater && creditCardAvailable) {
      result.push(changeTab(Tabs.creditCard));
    }

    return result;
  });
};
