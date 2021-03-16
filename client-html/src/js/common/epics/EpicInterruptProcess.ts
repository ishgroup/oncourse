/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../epics/EpicUtils";
import ProcessService from "../services/ProcessService";
import { INTERRUPT_PROCESS, INTERRUPT_PROCESS_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: INTERRUPT_PROCESS,
  getData: payload => ProcessService.interruptProcess(payload.processId),
  processData: () => {
    return [
      {
        type: INTERRUPT_PROCESS_FULFILLED,
        payload: { process: null }
      }
    ];
  }
};

export const EpicInterruptProcess: Epic<any, any> = EpicUtils.Create(request);
