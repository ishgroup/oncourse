import { IAction } from "../../../../common/actions/IshAction";
import { GET_PAYMENT_IN_CUSTOM_VALUES_FULFILLED } from "../actions";
import { PaymentInState } from "./state";

export const paymentInReducer = (state: PaymentInState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PAYMENT_IN_CUSTOM_VALUES_FULFILLED: {
      return {
        ...state,
        customValues: action.payload
      };
    }

    default: {
      return {
        ...state
      };
    }
  }
};
