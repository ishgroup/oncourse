/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import { CONTEXT } from "../../../../../api/Constants";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { GET_PRINT_RESULT } from "../actions";

const request: EpicUtils.Request = {
  type: GET_PRINT_RESULT,
  hideLoadIndicator: true,
  getData: ({entityName, processId}) => {
    window.open(`${CONTEXT}v1/list/export/pdf/${processId}?entityName=${entityName}`);
    return new Promise(resolve => resolve(null));
  },
  processData: () => [
    stopSubmit("ListShareForm"),
  ],
  processError: response => [stopSubmit("ListShareForm"), ...FetchErrorHandler(response)],
};

export const EpicGetPrintResult: Epic<any, any> = EpicUtils.Create(request);
