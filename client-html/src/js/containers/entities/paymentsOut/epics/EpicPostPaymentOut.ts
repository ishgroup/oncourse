/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { PaymentOut } from "@api/model";
import { POST_PAYMENT_OUT_ITEM, POST_PAYMENT_OUT_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import PaymentOutService from "../services/PaymentOutService";

const request: EpicUtils.Request<any, { paymentOut: PaymentOut }> = {
  type: POST_PAYMENT_OUT_ITEM,
  getData: ({ paymentOut }) => PaymentOutService.postPaymentOut(paymentOut),
  processData: () => {
    return [
      {
        type: POST_PAYMENT_OUT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "PaymentOut Record created" }
      }
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response)];
  }
};

export const EpicPostPaymentOut: Epic<any, any> = EpicUtils.Create(request);
