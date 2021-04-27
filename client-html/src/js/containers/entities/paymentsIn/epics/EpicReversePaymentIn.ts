/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PAYMENT_IN_ITEM, REVERSE_PAYMENT_IN_ITEM, REVERSE_PAYMENT_IN_ITEM_FULFILLED } from "../actions";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import PaymentInService from "../services/PaymentInService";

const request: EpicUtils.Request<any, number> = {
  type: REVERSE_PAYMENT_IN_ITEM,
  getData: id => PaymentInService.reverse(id),
  processData: (v, s, id ) => [
      {
        type: REVERSE_PAYMENT_IN_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "PaymentIn Record reversed" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PaymentIn", listUpdate: true, savedID: id }
      },
      {
        type: GET_PAYMENT_IN_ITEM,
        payload: id
      }
    ]
};

export const EpicReversePaymentIn: Epic<any, any> = EpicUtils.Create(request);
