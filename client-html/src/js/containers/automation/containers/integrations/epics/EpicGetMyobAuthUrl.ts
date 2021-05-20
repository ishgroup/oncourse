/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import IntegrationService from "../services";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_MYOB_AUTH_URL_FULFILLED, GET_MYOB_AUTH_URL_REQUEST } from "../../../actions";

const request: EpicUtils.Request = {
  type: GET_MYOB_AUTH_URL_REQUEST,
  hideLoadIndicator: true,
  getData: () => IntegrationService.getMyobIntegrationAuthUrl(),
  processData: (url: string) => {
    return [
      {
        type: GET_MYOB_AUTH_URL_FULFILLED,
        payload: { url, type: "myobAuthUrl" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response);
  }
};

export const EpicGetMyobAuthUrl: Epic<any, any> = EpicUtils.Create(request);
