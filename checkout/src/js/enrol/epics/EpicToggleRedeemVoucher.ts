import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";

import {
  getAmount, setPayer, setRedeemVoucherActivity,
  TOGGLE_REDEEM_VOUCHER,
} from "../actions/Actions";
import {IAction} from "../../actions/IshAction";

export const EpicToggleRedeemVoucher: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any, IshState>): Observable<any> => {
  return action$.ofType(TOGGLE_REDEEM_VOUCHER).flatMap((action:IAction<any>) => {

    const payload = action.payload;
    const payerId = payload.voucher.payer && payload.voucher.payer.id;

    return payerId && payload.enabled ?
    [
      setRedeemVoucherActivity(payload.voucher.id, payload.enabled),
      setPayer(payerId),
      getAmount(),
    ] :
    [
      setRedeemVoucherActivity(payload.voucher.id, payload.enabled),
      getAmount(),
    ];
  });
};

