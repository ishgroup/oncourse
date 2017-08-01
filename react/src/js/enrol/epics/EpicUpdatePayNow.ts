import {Epic, ActionsObservable} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {IAction} from "../../actions/IshAction";
import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {UPDATE_PAYNOW, updateAmount} from "../actions/Actions";
import {showCommonError} from "./EpicUtils";

export const EpicUpdatePayNow: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  let currentValidValue = 0;

  return action$.ofType(UPDATE_PAYNOW).flatMap((action: IAction<any>): any => {
    const state = store.getState();
    const amount = state.checkout.amount;
    const newAmount = {...amount, payNow: action.payload.val};
    const errors = action.payload.validate ? CheckoutService.validatePayNow(newAmount) : [];

    currentValidValue = !CheckoutService.validatePayNow(newAmount).length && action.payload.validate
      ? action.payload.val
      : currentValidValue || amount.minPayNow;

    if (errors.length) {
      return [
        updateAmount({...amount, payNow: currentValidValue}),
        showCommonError({message: errors[0]}),
      ];
    }

    return [
      updateAmount(newAmount),
    ];
  });
};
