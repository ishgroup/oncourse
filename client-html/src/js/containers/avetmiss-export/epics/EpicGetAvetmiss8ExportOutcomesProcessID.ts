/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import AvetmissExportService from "../services/AvetmissExportService";
import { GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID, GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID,
  getData: payload => AvetmissExportService.getExportOutcomesProcessID(payload),
  processData: (outcomesID: string) => {
    return [
      {
        type: GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED,
        payload: { outcomesID }
      }
    ];
  }
};

export const EpicGetAvetmiss8ExportOutcomesProcessID: Epic<any, any> = EpicUtils.Create(request);
