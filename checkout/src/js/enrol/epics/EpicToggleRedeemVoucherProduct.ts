import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {
  getAmount, setRedeemVoucherProductActivity,
  TOGGLE_REDEEM_VOUCHER_PRODUCT,
} from "../actions/Actions";
import {IAction} from "../../actions/IshAction";

const EpicToggleRedeemVoucherProduct: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any, IshState>): Observable<any> => {
  return action$.ofType(TOGGLE_REDEEM_VOUCHER_PRODUCT).flatMap((action:IAction<any>) => {

    const payload = action.payload;

    return [
      setRedeemVoucherProductActivity(payload.voucher.id, payload.enabled),
      getAmount(),
    ]
  });
};

export default EpicToggleRedeemVoucherProduct;

