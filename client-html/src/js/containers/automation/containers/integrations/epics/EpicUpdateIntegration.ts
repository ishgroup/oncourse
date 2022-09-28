/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import IntegrationService from "../services";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getIntegrations, UPDATE_INTEGRATION_ITEM_REQUEST } from "../../../actions";
import history from "../../../../../constants/History";
import { parseIntegrations } from "../utils";

const request: EpicUtils.Request = {
  type: UPDATE_INTEGRATION_ITEM_REQUEST,
  getData: payload => IntegrationService.updateIntegration(payload.id, payload.item),
  processData: (response, state, { form, item }) => {
    if (state.nextLocation) {
      history.push(state.nextLocation);
    }
    return [
      initialize(form, parseIntegrations([item])[0]),
      getIntegrations(),
      { type: FETCH_SUCCESS, payload: { message: "Integration was successfully updated" } }
    ];
  },
  processError: response => FetchErrorHandler(response, "Error. Integration was not updated")
};

export const EpicUpdateIntegration: Epic<any, any> = EpicUtils.Create(request);
