/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ProcessResult } from "@api/model";
import { Epic } from "redux-observable";
import { CLEAR_ACTION_ON_FAIL, CLEAR_PROCESS, FETCH_FAIL, START_PROCESS, UPDATE_PROCESS } from "../actions";
import { IAction } from "../actions/IshAction";
import ProcessService from "../services/ProcessService";

import * as EpicUtils from "./EpicUtils";

const switchByStatus = (
  process: ProcessResult = {},
  processId: string,
  actions: IAction<any>[],
  actionsOnFail: IAction<any>[]
): IAction<any>[] => {
  switch (process.status) {
    case "Finished": {
      return [
        {
          type: UPDATE_PROCESS,
          payload: { process, processId }
        },
        ...actions,
        {
          type: CLEAR_PROCESS
        }
      ];
    }
    case "In progress": {
      return [
        {
          type: UPDATE_PROCESS,
          payload: { process, processId }
        },
        {
          type: START_PROCESS,
          payload: { processId, actions, actionsOnFail }
        }
      ];
    }
    case "Not found": {
      return [
        {
          type: FETCH_FAIL,
          payload: { message: process.message || "Process not found" }
        },
        {
          type: CLEAR_PROCESS
        }
      ];
    }
    case "Failed":
    default: {
      return [
        {
          type: FETCH_FAIL,
          payload: { message: process.message || "Process failed" }
        },
        ...(actionsOnFail !== undefined ? actionsOnFail : [{ type: CLEAR_ACTION_ON_FAIL }]),
        {
          type: CLEAR_PROCESS
        }
      ];
    }
  }
};

const request: EpicUtils.DelayedRequest = {
  type: START_PROCESS,
  delay: 1000,
  hideLoadIndicator: true,
  getData: payload => ProcessService.getProcessStatus(payload.processId),
  processData: (process: ProcessResult, state, payload) =>
    switchByStatus(process, payload.processId, payload.actions, payload.actionsOnFail),
  processError: (res, payload) =>
    switchByStatus(res.data, res.data.processId, res.data.actions, res.data.actionsOnFail)
};

export const EpicManageProcess: Epic<any, any> = EpicUtils.CreateWithTimeout(request);
