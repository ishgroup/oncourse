/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Column, DataResponse, DataRow, Script } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_ON_DEMAND_SCRIPTS, GET_ON_DEMAND_SCRIPTS_FULFILLED } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../common/services/EntityService";

const request: EpicUtils.Request<any, any> = {
  type: GET_ON_DEMAND_SCRIPTS,
  getData: () => EntityService.getRecords(
    "Script",
    `( entityClass == null ) && ( triggerType == ON_DEMAND ) && ( enabled )`
  ),
  processData: (records: DataResponse) => {
    const nameIndex = records.columns.findIndex((col: Column) => col.attribute === "name");
    const scripts: Script[] = records.rows.map((row: DataRow) => ({ id: Number(row.id), name: row.values[nameIndex] } as Script));

    return [
      {
        type: GET_ON_DEMAND_SCRIPTS_FULFILLED,
        payload: { scripts }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get scripts")
};

export const EpicGetDashboardScripts: Epic<any, any> = EpicUtils.Create(request);
