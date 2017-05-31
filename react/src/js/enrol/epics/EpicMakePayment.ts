import {ProcessError, Request} from "./EpicUtils";
import {MAKE_PAYMENT, MAKE_PAYMENT_REQUEST} from "../containers/payment/actions/Actions";

import CheckoutService from "../services/CheckoutService";

import {PaymentResponse} from "../../model/checkout/payment/PaymentResponse";
import {IshState} from "../../services/IshState";
import {AxiosResponse} from "axios";
import {CheckoutModel} from "../../model/checkout/CheckoutModel";
import {ProcessCheckoutModel} from "./EpicProceedToPayment";
import {Epic} from "redux-observable";
import * as EpicUtils from "./EpicUtils";

const request: Request<PaymentResponse, IshState> = {
  type: MAKE_PAYMENT_REQUEST,
  getData: (payload: any, state: IshState): Promise<PaymentResponse>  => {
    return CheckoutService.makePayment(payload, state);
  },
  processData: (response: PaymentResponse, state: IshState): any[] => {
    return [{type: MAKE_PAYMENT, payload: response}]
  },
  processError: (response: AxiosResponse): { type: string, payload?: any}[] => {
    const data: any = response.data;
    if (data.payerId && data.amount && data.contactNodes) {
      return ProcessCheckoutModel.process(data as CheckoutModel);
    } else {
      return ProcessError(response);
    }
  }
};

export const EpicMakePayment: Epic<any, any> = EpicUtils.Create(request);


