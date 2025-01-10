/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse, DataRow, Script } from "@api/model";
import { Epic } from "redux-observable";
import { GET_ON_DEMAND_SCRIPTS, GET_ON_DEMAND_SCRIPTS_FULFILLED } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import EntityService from "../../../common/services/EntityService";

const request: EpicUtils.Request<DataResponse> = {
  type: GET_ON_DEMAND_SCRIPTS,
  getData: () => EntityService.getPlainRecords(
    "Script",
    "name,shortDescription",
    `( entityClass == null ) && ( triggerType == ON_DEMAND ) && ( automationStatus == ENABLED )`
  ),
  processData: records => {
    const scripts: Script[] = records.rows.map((row: DataRow) => ({
      id: Number(row.id),
      name: row.values[0],
      description: row.values[1]
    }));

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
