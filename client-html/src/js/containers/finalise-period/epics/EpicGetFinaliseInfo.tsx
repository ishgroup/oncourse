/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Create, Request } from "../../../common/epics/EpicUtils";
import { GET_FINALISE_INFO } from "../actions";
import FinaliseService from "../services";
import { FinalisePeriodInfo } from "@api/model";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: Request = {
  type: GET_FINALISE_INFO,
  getData: (lockDate: string) => FinaliseService.getInfo(lockDate),
  processData: (info: FinalisePeriodInfo) => {
    return [initialize("FinaliseForm", info)];
  },
  processError: response => FetchErrorHandler(response, "Unable to get finalise period info")
};

export const EpicGetFinaliseInfo: Epic<any, any> = Create(request);
