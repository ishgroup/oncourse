/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse, Script } from "@api/model";
import { Create, Request } from "./EpicUtils";
import { GET_SCRIPTS_FULFILLED, GET_SCRIPTS_REQUEST } from "../actions";
import EntityService from "../services/EntityService";
import { getCustomColumnsMap } from "../utils/common";

const scriptsMap = {
  "ProductItem": ["ProductItem", "Voucher", "Article", "Membership"],
  "AbstractInvoice": ["Invoice", "Quote"]
};

const request: Request = {
  type: GET_SCRIPTS_REQUEST,
  getData: payload => EntityService.getPlainRecords(
    "Script",
    "name,entity",
    `( ${
      scriptsMap[payload.entity]
        ? scriptsMap[payload.entity].map(e => `entityClass == ${e}`).join(" || ")
        : `entityClass == ${payload.entity}`
    } ) && ( triggerType == ON_DEMAND ) && (automationStatus = ENABLED)`
  ),
  processData: (records: DataResponse) => {
    const scripts: Script[] = records.rows.map(getCustomColumnsMap("name,entity"));

    return [
      {
        type: GET_SCRIPTS_FULFILLED,
        payload: scripts
      }
    ];
  }
};

export const EpicGetScripts: Epic<any, any> = Create(request);
