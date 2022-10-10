/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED,
  GET_REFUNDABLE_PAYMENTS_FULFILLED
} from "../actions";
import { PaymentOutState } from "./state";

export const paymentOutReducer = (state: PaymentOutState = {}, action: IAction): any => {
  switch (action.type) {
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
