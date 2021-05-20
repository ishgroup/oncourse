/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import AvetmissExportService from "../services/AvetmissExportService";
import { CLEAR_AVETMISS8_EXPORT_ID, GET_AVETMISS8_OUTCOMES_STATUS, GET_AVETMISS_EXPORT_OUTCOMES } from "../actions";
import { ProcessResult } from "@api/model";
import { CLEAR_PROCESS, FETCH_FAIL, UPDATE_PROCESS } from "../../../common/actions";
import { State } from "../../../reducers/state";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const switchByStatus = (process, storedID) => {
  switch (process.status) {
    case "Finished": {
      return [
        {
          type: GET_AVETMISS_EXPORT_OUTCOMES,
          payload: storedID
        }
      ];
    }
    case "In progress": {
      return [
        {
          type: GET_AVETMISS8_OUTCOMES_STATUS,
          payload: storedID
        }
      ];
    }
    case "Not found": {
      return [
        {
          type: CLEAR_AVETMISS8_EXPORT_ID
        },
        {
          type: CLEAR_PROCESS
        },
        {
          type: FETCH_FAIL,
          payload: { message: process.message || "Process not found" }
        }
      ];
    }
    case "Failed":
    default: {
      return [
        {
          type: CLEAR_AVETMISS8_EXPORT_ID
        },
        {
          type: CLEAR_PROCESS
        },
        {
          type: FETCH_FAIL,
          payload: { message: process.message || "Process failed" }
        }
      ];
    }
  }
};

const request: EpicUtils.DelayedRequest = {
  type: GET_AVETMISS8_OUTCOMES_STATUS,
  delay: 1000,
  getData: (exportID: string) => {
    return AvetmissExportService.getExportStatus(exportID);
  },
  processData: (process: ProcessResult, state: State) => {
    return [
      {
        type: UPDATE_PROCESS,
        payload: { process }
      },
      ...(state.export.outcomesID ? switchByStatus(process, state.export.outcomesID) : [])
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

export const EpicProcessAvetmiss8Outcomes: Epic<any, any> = EpicUtils.CreateWithTimeout(request);
