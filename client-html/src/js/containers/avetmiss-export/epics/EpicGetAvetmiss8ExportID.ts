/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import AvetmissExportService from "../services/AvetmissExportService";
import { CLEAR_AVETMISS8_EXPORT_ID, GET_AVETMISS8_EXPORT_ID, GET_AVETMISS8_EXPORT_ID_FULFILLED } from "../actions";
import { CLEAR_PROCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_AVETMISS8_EXPORT_ID,
  getData: payload => AvetmissExportService.exportAvetmiss8(payload),
  processData: (exportID: string) => {
    return [
      {
        type: GET_AVETMISS8_EXPORT_ID_FULFILLED,
        payload: { exportID }
      }
    ];
  },
  processError: response => {
    return [
      {
        type: CLEAR_AVETMISS8_EXPORT_ID
      },
      {
        type: CLEAR_PROCESS
      },
      ...FetchErrorHandler(response)
    ];
  }
};

export const EpicGetAvetmiss8ExportID: Epic<any, any> = EpicUtils.Create(request);
