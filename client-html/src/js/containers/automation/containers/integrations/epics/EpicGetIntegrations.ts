/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import IntegrationService from "../services";
import { parseIntegrations } from "../utils/index";
import { GET_INTEGRATIONS_FULFILLED, GET_INTEGRATIONS_REQUEST } from "../../../actions";

const request: EpicUtils.Request = {
  type: GET_INTEGRATIONS_REQUEST,
  getData: () => IntegrationService.getIntegrations(),
  processData: response => {
    const integrations = parseIntegrations(response);

    return [
      {
        type: GET_INTEGRATIONS_FULFILLED,
        payload: { integrations }
      }
    ];
  }
};

export const EpicGetIntegrations: Epic<any, any> = EpicUtils.Create(request);
