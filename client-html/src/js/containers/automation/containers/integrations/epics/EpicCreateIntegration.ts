/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../../common/actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { parseIntegrations } from "../utils/index";
import IntegrationService from "../services";
import { CREATE_INTEGRATION_ITEM_FULFILLED, CREATE_INTEGRATION_ITEM_REQUEST } from "../../../actions";

const request: EpicUtils.Request<any, any, any> = {
  type: CREATE_INTEGRATION_ITEM_REQUEST,
  getData: payload => IntegrationService.createIntegration(payload.item),
  retrieveData: () => IntegrationService.getIntegrations(),
  processData: response => {
    const integrations = parseIntegrations(response);

    return [
      {
        type: CREATE_INTEGRATION_ITEM_FULFILLED,
        payload: { integrations }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Integration was successfully created" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Integration was not created");
  }
};

export const EpicCreateIntegration: Epic<any, any> = EpicUtils.Create(request);
