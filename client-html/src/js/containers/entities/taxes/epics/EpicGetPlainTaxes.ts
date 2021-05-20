/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { GET_PLAIN_TAX_ITEMS, GET_PLAIN_TAX_ITEMS_FULFILLED } from "../actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { DataResponse, Tax } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_PLAIN_TAX_ITEMS,
  getData: () => {
    return EntityService.getPlainRecords("Tax", "id,taxCode,rate,isGSTTaxType", "taxCode !== '*'");
  },
  processData: (records: DataResponse) => {
    const items: Tax[] = [];
    for (const row of records.rows) {
      items.push({
        id: Number(row.values[0]),
        code: row.values[1],
        rate: parseFloat(row.values[2]),
        gst: row.values[3] === "true"
      });
    }

    items.sort((a, b) => (a.code[0] > b.code[0] ? 1 : -1));

    return [
      {
        type: GET_PLAIN_TAX_ITEMS_FULFILLED,
        payload: { items }
      }
    ];
  }
};

export const EpicGetPlainTaxes: Epic<any, any> = EpicUtils.Create(request);
