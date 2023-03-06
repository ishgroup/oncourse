/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PAYMENT_IN_CUSTOM_VALUES, GET_PAYMENT_IN_CUSTOM_VALUES_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request<any,  number> = {
  type: GET_PAYMENT_IN_CUSTOM_VALUES,
  getData: id =>
    EntityService.getPlainRecords(
      "PaymentIn",
      "status,gatewayReference,payer.totalOwing,reconciled,reversalOf,reversedBy,banking.id,amount,payer.fullName",
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
          gatewayReference: row.values[1],
          payerTotalOwing: Number(row.values[2]),
          reconciled: row.values[3] === "true",
          reversalOf: row.values[4],
          reversedBy: row.values[5],
          bankingId: row.values[6],
          amount: parseFloat(row.values[7]),
          name: row.values[8],
        }
      }
    ];
  }
};

export const EpicGetCustomValues: Epic<any, any> = EpicUtils.Create(request);
