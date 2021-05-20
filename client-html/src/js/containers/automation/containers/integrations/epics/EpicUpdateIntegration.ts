/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import IntegrationService from "../services";
import { FETCH_SUCCESS } from "../../../../../common/actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { parseIntegrations } from "../utils/index";
import { UPDATE_INTEGRATION_ITEM_FULFILLED, UPDATE_INTEGRATION_ITEM_REQUEST } from "../../../actions";

const request: EpicUtils.Request = {
  type: UPDATE_INTEGRATION_ITEM_REQUEST,
  getData: payload => IntegrationService.updateIntegration(payload.id, payload.item),
  retrieveData: () => IntegrationService.getIntegrations(),
  processData: response => {
    const integrations = parseIntegrations(response);

    return [
      { type: UPDATE_INTEGRATION_ITEM_FULFILLED, payload: { integrations } },
      { type: FETCH_SUCCESS, payload: { message: "Integration was successfully updated" } }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Integration was not updated");
  }
};

export const EpicUpdateIntegration: Epic<any, any> = EpicUtils.Create(request);
