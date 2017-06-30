import * as L from "lodash";

import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {ActionsObservable, combineEpics, Epic} from "redux-observable";

import uuid from "uuid";

import {Create, ProcessError, Request} from "../../../epics/EpicUtils";
import {
  MAKE_PAYMENT,
  PROCESS_PAYMENT,
  processPayment,
  resetPaymentState,
  updatePaymentStatus,
} from "../actions/Actions";

import CheckoutService from "../../../services/CheckoutService";

import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {IshState} from "../../../../services/IshState";
import {AxiosResponse} from "axios";
import {CheckoutModel} from "../../../../model/checkout/CheckoutModel";
import {ProcessCheckoutModel} from "../../../epics/EpicProceedToPayment";
import {IAction} from "../../../../actions/IshAction";
import {Values} from "../services/PaymentService";
import {PaymentStatus} from "../../../../model/checkout/payment/PaymentStatus";
import {GetPaymentStatus} from "./EpicGetPaymentStatus";
import {Phase} from "../../../reducers/State";
import {changePhase} from "../../../actions/Actions";

const request: Request<PaymentResponse, IshState> = {
  type: PROCESS_PAYMENT,
  getData: (payload: any, state: IshState): Promise<PaymentResponse> => {
    return CheckoutService.makePayment(payload, state);
  },
  processData: (response: PaymentResponse, state: IshState): IAction<any>[] | Observable<any> => {
    return CheckoutService.processPaymentResponse(response);
  },
  processError: (response: AxiosResponse): IAction<any>[] => {
    const data: any = response.data;
    if (data.payerId && data.amount && data.contactNodes) {
      return ProcessCheckoutModel.process(data as CheckoutModel);
    } else {
      return L.concat([changePhase(Phase.Payment), resetPaymentState()], ProcessError(response));
    }
  },
};

/**
 * Send Payment Request and Process Payment Response
 */
const ProcessPayment: Epic<any, any> = Create(request);


/**
 * Init PaymentState and Start Payment Process
 */
const MakePayment: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(MAKE_PAYMENT).flatMap((action: IAction<Values>) => {
    const response: PaymentResponse = new PaymentResponse();
    response.sessionId = uuid();
    response.status = PaymentStatus.IN_PROGRESS;
    return [updatePaymentStatus(response), changePhase(Phase.Result), processPayment(action.payload)];
  });
};


export const EpicPayment = combineEpics(MakePayment, ProcessPayment, GetPaymentStatus);
