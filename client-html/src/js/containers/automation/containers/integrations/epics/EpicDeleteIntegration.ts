/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import IntegrationService from "../services";
import { FETCH_SUCCESS } from "../../../../../common/actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { DELETE_INTEGRATION_ITEM_FULFILLED, DELETE_INTEGRATION_ITEM_REQUEST } from "../../../actions";
import { parseIntegrations } from "../utils";

const request: EpicUtils.Request<any, any, any> = {
  type: DELETE_INTEGRATION_ITEM_REQUEST,
  getData: payload => IntegrationService.deleteIntegrationItem(payload.id),
  retrieveData: () => IntegrationService.getIntegrations(),
  processData: response => {
    const integrations = parseIntegrations(response);

    return [
      {
        type: DELETE_INTEGRATION_ITEM_FULFILLED,
        payload: { integrations }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Integration was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Integration was not deleted");
  }
};

export const EpicDeleteIntegration: Epic<any, any> = EpicUtils.Create(request);
