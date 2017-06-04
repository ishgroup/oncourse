import {IshState} from "../../../../services/IshState";
import CheckoutService from "../../../services/CheckoutService";
import {IAction} from "../../../../actions/IshAction";
import {GET_PAYMENT_STATUS, updatePaymentStatus} from "../actions/Actions";
import {Reply, Request} from "../../../epics/EpicUtils";
import {Observable} from "rxjs/Observable";
import {Epic} from "redux-observable";
import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {changePhase} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";

const DELAY = 5000;

const request: Request<PaymentResponse, IshState> = {
  type: GET_PAYMENT_STATUS,
  getData: (payload: any, state: IshState): Promise<PaymentResponse> => {
    return CheckoutService.getPaymentStatus(state.checkout.payment)
  },
  processData: (response: PaymentResponse, state: IshState): IAction<any>[] | Observable<any> => {
    if (CheckoutService.isPaymentInProgress(response)) {
      return Observable.of({type: GET_PAYMENT_STATUS}).delay(DELAY);
    } else {
      return [updatePaymentStatus(response), changePhase(Phase.Result)]
    }
  }
};

export const GetPaymentStatus: Epic<any, any> = Reply(request, 3);