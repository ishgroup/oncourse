import {Observable} from "rxjs/Observable";
import {combineEpics, Epic} from "redux-observable";
import {Create, ProcessError, Request} from "../../../../common/epics/EpicUtils";
import {
  GET_CORPORATE_PASS_REQUEST,
  SUBMIT_PAYMENT_CORPORATE_PASS,
  SUBMIT_PAYMENT_FOR_WAITING_COURSES,
  PROCESS_PAYMENT_V2_FAILED_STATUS,
  PROCESS_PAYMENT_V2,
  setPaymentData,
  resetPaymentState,
  applyCorporatePass,
  generateWaitingCoursesResultData,
} from "../actions/Actions";
import CheckoutService, {BuildCheckoutModelRequest, BuildWaitingCoursesResult} from "../../../services/CheckoutService";
import {PaymentStatus, CorporatePass, PaymentResponse, CheckoutModel, ContactNode} from "../../../../model";
import {IshState} from "../../../../services/IshState";
import {IAction} from "../../../../actions/IshAction";
import {GetPaymentStatus} from "./EpicGetPaymentStatus";
import {changePhase, getAmount, showFormValidation, togglePayNowVisibility} from "../../../actions/Actions";
import {FULFILLED} from "../../../../common/actions/ActionUtils";
import CheckoutServiceV2 from "../../../services/CheckoutServiceV2";
import {AxiosResponse} from "axios";
import {ProcessCheckoutModel} from "../../../epics/EpicProceedToPayment";
import {Phase} from "../../../reducers/State";
import {EpicValidatePayment} from "./EpicValidatePayment";

const SubmitPaymentForWaitingCoursesRequest: Request<any, IshState> = {
  type: SUBMIT_PAYMENT_FOR_WAITING_COURSES,
  getData: (payload: any, state: IshState): Promise<any> => {
    const paymentRequest = {
      checkoutModelRequest: BuildCheckoutModelRequest.fromState(state),
      merchantReference: state.checkout.payment.merchantReference,
      sessionId: state.checkout.payment.sessionId,
      ccAmount: state.checkout.amount.ccPayment,
      storeCard: false,
    };
    return CheckoutServiceV2.makePayment(paymentRequest, false, state.checkout.payerId);
  },
  processData: (response: any, state: IshState): IAction<any>[] | Observable<any> => {
    return CheckoutService.processPaymentResponse(
      {status: PaymentStatus.SUCCESSFUL_WAITING_COURSES},
      [generateWaitingCoursesResultData(BuildWaitingCoursesResult.fromState(state))],
    );
  },
  processError: response => {
    const actions = [];

    response.data.contactNodes?.forEach((cn: ContactNode) => {
      cn.waitingLists.forEach(wl => {
        actions.push(showFormValidation(response,`${wl.contactId}-${wl.courseId}`));
      });
    });

    return actions;
  }
};

const SubmitPaymentForWaitingCourses: Epic<any, any> = Create(SubmitPaymentForWaitingCoursesRequest);

/**
 * Init PaymentState and Start Payment Process by Corporate Pass
 */
const SubmitPaymentCorporatePassRequest: Request<any, IshState> = {
  type: SUBMIT_PAYMENT_CORPORATE_PASS,
  getData: (payload: any, state: IshState): Promise<any> => {
    return CheckoutService.submitPaymentCorporatePass(payload, state);
  },
  processData: (): IAction<any>[] | Observable<any> => {
    return CheckoutService.processPaymentResponse({status: PaymentStatus.SUCCESSFUL_BY_PASS});
  },
};

const SubmitPaymentCorporatePass: Epic<any, any> = Create(SubmitPaymentCorporatePassRequest);

const corporatePassRequest: Request<CorporatePass, IshState> = {
  type: GET_CORPORATE_PASS_REQUEST,
  getData: (payload: any, state: IshState): Promise<CorporatePass> => {
    return CheckoutService.getCorporatePass(payload, state);
  },
  processData: (response: CorporatePass): IAction<any>[] | Observable<any> => {
    return [
      {type: FULFILLED(GET_CORPORATE_PASS_REQUEST)},
      applyCorporatePass(response),
      getAmount(),
      togglePayNowVisibility(false),
    ];
  },

};

const GetCorporatePass: Epic<any, any> = Create(corporatePassRequest);

const processPaymentV2: Request<PaymentResponse, IshState> = {
  type: PROCESS_PAYMENT_V2,
  getData: ({xValidateOnly,sessionId}, state: IshState) => {
    const paymentRequest = {
      sessionId: xValidateOnly ? null : sessionId,
      checkoutModelRequest: BuildCheckoutModelRequest.fromState(state),
      merchantReference: xValidateOnly ? null : state.checkout.payment.merchantReference,
      ccAmount: state.checkout.amount.ccPayment,
      storeCard: false,
    };
    return CheckoutServiceV2.makePayment(paymentRequest,xValidateOnly,state.checkout.payerId);
  },
  processData: (response: PaymentResponse, state: IshState, {xValidateOnly}): IAction<any>[] | Observable<any> => {
    if (xValidateOnly) {
      return [setPaymentData(response)];
    }
    return CheckoutService.processPaymentResponse(response);
  },
  processError: (response: AxiosResponse): IAction<any>[] => {
    if (response && response.data && response.data.payerId && response.data.amount && response.data.contactNodes) {
      return [...ProcessCheckoutModel.process(response.data as CheckoutModel),...ProcessError(response)];
    } else {
      return [
        changePhase(Phase.Payment),
        resetPaymentState(),
        ...ProcessError(response),
      ];
    }
  },
};

const ProcessPaymentV2: Epic<any, any> = Create(processPaymentV2);

const processPaymentV2Status: Request<PaymentResponse, IshState> = {
  type: PROCESS_PAYMENT_V2_FAILED_STATUS,
  getData: (p, state: IshState) => {
    return CheckoutServiceV2.getStatus(
      state.checkout.payment.sessionId,
      state.checkout.payerId,
    );
  },
  processData: (response: PaymentResponse): IAction<any>[] | Observable<any> => {
    response.status = "FAILED";
    return CheckoutService.processPaymentResponse(response);
  },
  processError: (response: AxiosResponse): IAction<any>[] => {
    const data: any = response.data;
    if (data && data.payerId && data.amount && data.contactNodes) {
      return [...ProcessCheckoutModel.process(data as CheckoutModel), ...ProcessError(response)];
    } else {
      return [
        changePhase(Phase.Payment),
        resetPaymentState(),
        ...ProcessError(response),
      ];
    }
  },
};

const ProcessPaymentV2Status: Epic<any, any> = Create(processPaymentV2Status);

export const EpicPayment = combineEpics(
  SubmitPaymentCorporatePass,
  SubmitPaymentForWaitingCourses,
  GetPaymentStatus,
  EpicValidatePayment,
  GetCorporatePass,
  ProcessPaymentV2,
  ProcessPaymentV2Status,
);
