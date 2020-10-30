import {IshState} from "../../../../services/IshState";
import CheckoutService from "../../../services/CheckoutService";
import {IAction} from "../../../../actions/IshAction";
import {GET_PAYMENT_STATUS} from "../actions/Actions";
import {Reply, Request} from "../../../../common/epics/EpicUtils";
import {Observable} from "rxjs/Observable";
import {Epic} from "redux-observable";
import {PaymentResponse} from "../../../../model";
import CheckoutServiceV2 from "../../../services/CheckoutServiceV2";

const request: Request<PaymentResponse, IshState> = {
  type: GET_PAYMENT_STATUS,
  getData: (payload: any, state: IshState): Promise<PaymentResponse> => {
    return CheckoutServiceV2.getStatus(state.checkout.payment.sessionId,state.checkout.payerId);
  },
  processData: (response: PaymentResponse): IAction<any>[] | Observable<any> => {
    return CheckoutService.processPaymentResponse(response);
  },
};

export const GetPaymentStatus: Epic<any, any> = Reply(request, 3);
