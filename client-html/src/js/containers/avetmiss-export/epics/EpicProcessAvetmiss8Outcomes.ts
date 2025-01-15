/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ProcessResult } from '@api/model';
import { Epic } from 'redux-observable';
import { CLEAR_PROCESS, FETCH_FAIL, UPDATE_PROCESS } from '../../../common/actions';
import FetchErrorHandler from '../../../common/api/fetch-errors-handlers/FetchErrorHandler';

import * as EpicUtils from '../../../common/epics/EpicUtils';
import ProcessService from '../../../common/services/ProcessService';
import { State } from '../../../reducers/state';
import { CLEAR_AVETMISS8_EXPORT_ID, GET_AVETMISS8_OUTCOMES_STATUS, GET_AVETMISS_EXPORT_OUTCOMES } from '../actions';

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

const request: EpicUtils.Request = {
  type: GET_AVETMISS8_OUTCOMES_STATUS,
  getData: (exportID: string) => {
    return ProcessService.startProcessTrack(exportID);
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

export const EpicProcessAvetmiss8Outcomes: Epic<any, any> = EpicUtils.Create(request);
