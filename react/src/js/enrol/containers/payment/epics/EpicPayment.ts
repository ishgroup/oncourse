import * as L from "lodash";
import {AxiosResponse} from "axios";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {ActionsObservable, combineEpics, Epic} from "redux-observable";
import uuid from "uuid";

import {Create, ProcessError, Request} from "../../../epics/EpicUtils";
import {
  applyCorporatePass,
  GET_CORPORATE_PASS,
  SUBMIT_PAYMENT_CREDIT_CARD,
  SUBMIT_PAYMENT_CORPORATE_PASS,
  PROCESS_PAYMENT,
  processPayment,
  resetPaymentState,
  updatePaymentStatus,
} from "../actions/Actions";

import CheckoutService from "../../../services/CheckoutService";

import {PaymentResponse, CheckoutModel, PaymentStatus, CorporatePass} from "../../../../model";
import {IshState} from "../../../../services/IshState";
import {ProcessCheckoutModel} from "../../../epics/EpicProceedToPayment";
import {IAction} from "../../../../actions/IshAction";
import {CreditCardFormValues} from "../services/PaymentService";
import {GetPaymentStatus} from "./EpicGetPaymentStatus";
import {Phase} from "../../../reducers/State";
import {changePhase, getAmount, togglePayNowVisibility} from "../../../actions/Actions";

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
 * Init PaymentState and Start Payment Process by Credit Card
 */
const SubmitPaymentCreditCard: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(SUBMIT_PAYMENT_CREDIT_CARD).flatMap((action: IAction<CreditCardFormValues>) => {
    const response: PaymentResponse = new PaymentResponse();
    response.sessionId = uuid();
    response.status = PaymentStatus.IN_PROGRESS;
    return [
      updatePaymentStatus(response),
      changePhase(Phase.Result),
      processPayment(action.payload),
    ];
  });
};

/**
 * Init PaymentState and Start Payment Process by Corporate Pass
 */
const SubmitPaymentCorporatePassRequest: Request<any, IshState> = {
  type: SUBMIT_PAYMENT_CORPORATE_PASS,
  getData: (payload: any, state: IshState): Promise<any> => {
    return CheckoutService.submitPaymentCorporatePass(payload, state);
  },
  processData: (response: any, state: IshState): IAction<any>[] | Observable<any> => {
    return CheckoutService.processPaymentResponse(response);
  },
};

const SubmitPaymentCorporatePass: Epic<any, any> = Create(SubmitPaymentCorporatePassRequest);

const corporatePassRequest: Request<CorporatePass, IshState> = {
  type: GET_CORPORATE_PASS,
  getData: (payload: any, state: IshState): Promise<CorporatePass> => {
    return CheckoutService.getCorporatePass(payload, state);
  },
  processData: (response: CorporatePass, state: IshState): IAction<any>[] | Observable<any> => {
    return [
      applyCorporatePass(response),
      getAmount(),
      togglePayNowVisibility(false),
    ];
  },
  processError: (response: AxiosResponse): IAction<any>[] => {
    console.log(response);
    return [];
  },

};

const GetCorporatePass: Epic<any, any> = Create(corporatePassRequest);

export const EpicPayment = combineEpics(
  SubmitPaymentCreditCard,
  SubmitPaymentCorporatePass,
  ProcessPayment,
  GetPaymentStatus,
  GetCorporatePass,
);
