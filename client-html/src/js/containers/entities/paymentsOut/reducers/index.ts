import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_ADD_PAYMENT_OUT_VALUES_FULFILLED,
  GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED,
  GET_REFUNDABLE_PAYMENTS_FULFILLED
} from "../actions";
import { PaymentOutState } from "./state";

export const paymentOutReducer = (state: PaymentOutState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_ADD_PAYMENT_OUT_VALUES_FULFILLED: {
      return {
        ...state,
        addPaymentOutValues: action.payload
      };
    }

    case GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED: {
      return {
        ...state,
        paymentOutMethods: action.payload
      };
    }

    case GET_REFUNDABLE_PAYMENTS_FULFILLED: {
      return {
        ...state,
        refundablePayments: action.payload
      };
    }

    default: {
      return {
        ...state
      };
    }
  }
};
