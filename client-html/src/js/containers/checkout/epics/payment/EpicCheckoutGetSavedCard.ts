/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { CHECKOUT_GET_SAVED_CARD, CHECKOUT_GET_SAVED_CARD_FULFILLED } from "../../actions/checkoutPayment";

const request: EpicUtils.Request<DataResponse, { payerId: number, paymentMethodId: number }> = {
  type: CHECKOUT_GET_SAVED_CARD,
  getData: ({ payerId, paymentMethodId }) => EntityService.getPlainRecords(
    "PaymentIn",
    "creditCardName,creditCardNumber,creditCardType",
    `payer.id is ${payerId} and paymentMethod.id is ${paymentMethodId} and status is SUCCESS and billingId not is nul`,
    1,
    0,
    "createdOn",
    false
  ),
  processData: data => [
    {
      type: CHECKOUT_GET_SAVED_CARD_FULFILLED,
      payload: {
        savedCreditCard: data.rows.length
          ? {
            creditCardName: data.rows[0].values[0],
            creditCardNumber: data.rows[0].values[1].replace(/\./g, "X"),
            creditCardType: data.rows[0].values[2]
          } : null
      }
    }
  ]
};

export const EpicCheckoutGetSavedCard: Epic<any, any> = EpicUtils.Create(request);
