/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ProcessResult } from "@api/model";
import { Epic } from "redux-observable";
import { CLEAR_PROCESS, FETCH_FAIL, UPDATE_PROCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { State } from "../../../reducers/state";
import { CLEAR_AVETMISS8_EXPORT_ID, GET_AVETMISS8_EXPORT_RESULTS, GET_AVETMISS8_EXPORT_STATUS } from "../actions";
import AvetmissExportService from "../services/AvetmissExportService";

const switchByStatus = (process, storedID) => {
  switch (process.status) {
    case "Finished": {
      return [
        {
          type: GET_AVETMISS8_EXPORT_RESULTS,
          payload: storedID
        }
      ];
    }
    case "In progress": {
      return [
        {
          type: GET_AVETMISS8_EXPORT_STATUS,
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
  type: GET_AVETMISS8_EXPORT_STATUS,
  delay: 1000,
  getData: (exportID: string) => {
    return AvetmissExportService.getExportStatus(exportID);
  },
  processData: (process: ProcessResult, state: State) => {
    return [
      {
        type: UPDATE_PROCESS,
        payload: { process, processId: state.export.exportID }
      },
      ...(state.export.exportID ? switchByStatus(process, state.export.exportID) : [])
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

export const EpicProcessAvetmiss8Export: Epic<any, any> = EpicUtils.CreateWithTimeout(request);
