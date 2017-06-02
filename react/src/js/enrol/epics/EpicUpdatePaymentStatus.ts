import {Request} from "./EpicUtils";
import {UPDATE_PAYMENT_STATUS, UPDATE_PAYMENT_STATUS_REQUEST} from "../containers/payment/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {PaymentResponse} from "../../model/checkout/payment/PaymentResponse";
import {IshState} from "../../services/IshState";
import {IAction} from "../../actions/IshAction";
const request: Request<PaymentResponse, IshState> = {
  type: UPDATE_PAYMENT_STATUS_REQUEST,
  getData: (payload: any, state: IshState) => {
    return CheckoutService.getPaymentStatus(state.checkout.payment)
  },
  processData: (response: PaymentResponse, state: IshState): IAction<any>[] => {
    return [{type: UPDATE_PAYMENT_STATUS, payload: response}]
  }


};