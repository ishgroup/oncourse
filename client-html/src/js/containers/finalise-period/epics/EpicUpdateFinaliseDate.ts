/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Create, Request } from "../../../common/epics/EpicUtils";
import { GET_FINALISE_INFO, UPDATE_FINALISE_DATE } from "../actions";
import FinaliseService from "../services";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../common/actions";

const request: Request = {
  type: UPDATE_FINALISE_DATE,
  getData: (lockDate: string) => FinaliseService.updateLockDate(lockDate),
  processData: () => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Accounting period finalised" }
      },
      {
        type: GET_FINALISE_INFO
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Error. Unable to finalise accounting period")
};

export const EpicUpdateFinaliseDate: Epic<any, any> = Create(request);
