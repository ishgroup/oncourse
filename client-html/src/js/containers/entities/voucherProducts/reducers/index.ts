/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { VoucherProductState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE,
  GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED
} from "../actions";

class VoucherProductStateClass implements VoucherProductState {
  maxFee: number = 0;

  minFee: number = 0;
}

export const voucherProductReducer = (
  state: VoucherProductState = new VoucherProductStateClass(),
  action: IAction<any>
): any => {
  switch (action.type) {
    case GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE: {
      return {
        ...state,
        maxFee: 0,
        minFee: 0
      };
    }

    default:
      return state;
  }
};
