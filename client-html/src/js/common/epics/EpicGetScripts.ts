/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse, DataRow, Script } from "@api/model";
import { Request, Create } from "./EpicUtils";
import { GET_SCRIPTS_FULFILLED, GET_SCRIPTS_REQUEST } from "../actions";
import EntityService from "../services/EntityService";

const scriptsMap = {
  "ProductItem": ["ProductItem", "Voucher", "Article", "Membership"],
  "AbstractInvoice": ["Invoice", "Quote"]
};

const request: Request = {
  type: GET_SCRIPTS_REQUEST,
  getData: payload => EntityService.getPlainRecords(
    "Script",
    "name",
    `( ${
      scriptsMap[payload.entity]
        ? scriptsMap[payload.entity].map(e => `entityClass == ${e}`).join(" || ")
        : `entityClass == ${payload.entity}`
    } ) && ( triggerType == ON_DEMAND ) && (automationStatus = ENABLED)`
  ),
  processData: (records: DataResponse) => {
    const scripts: Script[] = records.rows.map((row: DataRow) => ({ id: Number(row.id), name: row.values[0] } as Script));

    return [
      {
        type: GET_SCRIPTS_FULFILLED,
        payload: scripts
      }
    ];
  }
};

export const EpicGetScripts: Epic<any, any> = Create(request);
