/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_PAYMENT_IN_CUSTOM_VALUES, GET_PAYMENT_IN_CUSTOM_VALUES_FULFILLED } from "../actions";

const request: EpicUtils.Request<any,  number> = {
  type: GET_PAYMENT_IN_CUSTOM_VALUES,
  getData: id =>
    EntityService.getPlainRecords(
      "PaymentIn",
      "status,paymentMethod.type,gatewayReference,payer.totalOwing,reconciled,reversalOf,reversedBy,banking.id,amount,payer.fullName",
      `id == ${id}`,
      1,
      0
    ),
  processData: (response: DataResponse) => {
    const row = response.rows[0];
    return [
      {
        type: GET_PAYMENT_IN_CUSTOM_VALUES_FULFILLED,
        payload: {
          id: row.id,
          status: row.values[0],
          paymentMethodType: row.values[1],
          gatewayReference: row.values[2],
          payerTotalOwing: Number(row.values[3]),
          reconciled: row.values[4] === "true",
          reversalOf: row.values[5],
          reversedBy: row.values[6],
          bankingId: row.values[7],
          amount: parseFloat(row.values[8]),
          name: row.values[9],
        }
      }
    ];
  }
};

export const EpicGetCustomValues: Epic<any, any> = EpicUtils.Create(request);
