/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Column, DataResponse, DataRow, Script } from "@api/model";
import * as EpicUtils from "./EpicUtils";
import { GET_SCRIPTS_FULFILLED, GET_SCRIPTS_REQUEST } from "../actions";
import EntityService from "../services/EntityService";

const scriptsMap = { "ProductItem": ["ProductItem", "Voucher", "Article", "Membership"] };

const request: EpicUtils.Request = {
  type: GET_SCRIPTS_REQUEST,
  getData: payload => EntityService.getRecords(
    "Script",
    `( ${
      scriptsMap[payload.entity]
        ? scriptsMap[payload.entity].map(e => `entityClass == ${e}`).join(" || ")
        : `entityClass == ${payload.entity}`
    } ) && ( triggerType == ON_DEMAND )`
  ),
  processData: (records: DataResponse) => {
    const nameIndex = records.columns.findIndex((col: Column) => col.attribute === "name");
    const scripts: Script[] = records.rows.map((row: DataRow) => ({ id: Number(row.id), name: row.values[nameIndex] } as Script));

    return [
      {
        type: GET_SCRIPTS_FULFILLED,
        payload: scripts
      }
    ];
  }
};

export const EpicGetScripts: Epic<any, any> = EpicUtils.Create(request);
