/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import AvetmissExportService from "../services/AvetmissExportService";
import {
  CLEAR_AVETMISS8_EXPORT_ID,
  CLEAR_AVETMISS_EXPORT_OUTCOMES,
  GET_AVETMISS8_EXPORT_RESULTS,
  GET_AVETMISS8_EXPORT_RESULTS_FULFILLED,
  GET_FUNDING_UPLOADS_REQUEST
} from "../actions";
import { CLEAR_PROCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_AVETMISS8_EXPORT_RESULTS,
  getData: (exportID: string) => AvetmissExportService.getExport(exportID),
  processData: exported => {
    return [
      {
        type: GET_AVETMISS8_EXPORT_RESULTS_FULFILLED,
        payload: { exported }
      },
      {
        type: CLEAR_AVETMISS_EXPORT_OUTCOMES
      },
      {
        type: CLEAR_PROCESS
      },
      {
        type: GET_FUNDING_UPLOADS_REQUEST
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

export const EpicGetAvetmiss8ExportResults: Epic<any, any> = EpicUtils.Create(request);
