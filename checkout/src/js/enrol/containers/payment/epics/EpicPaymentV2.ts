import {Create, ProcessError, Request} from "../../../../common/epics/EpicUtils";
import {IshState} from "../../../../services/IshState";
import {PROCESS_PAYMENT_V2, resetPaymentState, setIframeUrl} from "../actions/Actions";
import {IAction} from "../../../../actions/IshAction";
import {Observable} from "rxjs/Observable";
import {AxiosResponse} from "axios";
import {ProcessCheckoutModel} from "../../../epics/EpicProceedToPayment";
import * as L from "lodash";
import {changePhase} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import {Epic} from "redux-observable";
import CheckoutServiceV2 from "../../../services/CheckoutServiceV2";
import {PaymentResponse} from "../../../../model/v2/checkout/payment/PaymentResponse";
import CheckoutService, {BuildCheckoutModelRequest} from "../../../services/CheckoutService";
import {CheckoutModel} from "../../../../model";


const request: Request<PaymentResponse, IshState> = {
  type: PROCESS_PAYMENT_V2,
  getData: ({xValidateOnly,payerId,referer}, state: IshState) => {
    const paymentRequest = {
      checkoutModelRequest: BuildCheckoutModelRequest.fromState(state),
      merchantReference: null,
      sessionId: null,
      ccAmount: state.checkout.amount.ccPayment,
      storeCard: false,
    };
    return CheckoutServiceV2.makePayment(paymentRequest,xValidateOnly,payerId,referer);
  },
  processData: (response: PaymentResponse, state: IshState, {xValidateOnly}): IAction<any>[] | Observable<any> => {
    if (xValidateOnly) {
      return [setIframeUrl(response.paymentFormUrl)];
    }
    return CheckoutService.processPaymentResponse(response);
  },
  processError: (response: AxiosResponse): IAction<any>[] => {
    const data: any = response.data;
    if (data && data.payerId && data.amount && data.contactNodes) {
      return ProcessCheckoutModel.process(data as CheckoutModel);
    } else {
      return L.concat([changePhase(Phase.Payment), resetPaymentState()], ProcessError(response));
    }
  },
};

export const ProcessPaymentV2: Epic<any, any> = Create(request);
