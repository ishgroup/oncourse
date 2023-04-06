/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import IntegrationService from "../services";
import { CREATE_INTEGRATION_ITEM_REQUEST, getIntegrations } from "../../../actions";
import { parseIntegrations } from "../utils";

const request: EpicUtils.Request = {
  type: CREATE_INTEGRATION_ITEM_REQUEST,
  getData: payload => IntegrationService.createIntegration(payload.item),
  processData: (v, s, { form, item }) => [
      initialize(form, parseIntegrations([item])[0]),
      getIntegrations(item.name),
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Integration was successfully created" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Error. Integration was not created")
};

export const EpicCreateIntegration: Epic<any, any> = EpicUtils.Create(request);
