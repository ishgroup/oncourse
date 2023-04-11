/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import IntegrationService from "../services";
import { parseIntegrations } from "../utils";
import { GET_INTEGRATIONS_REQUEST, getIntegrationsFulfilled } from "../../../actions";
import history from "../../../../../constants/History";

const request: EpicUtils.Request = {
  type: GET_INTEGRATIONS_REQUEST,
  getData: () => IntegrationService.getIntegrations(),
  processData: (response, s, { nameToSelect }) => {
    const integrations = parseIntegrations(response);
    integrations.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

    if (nameToSelect) {
      const item = integrations.find(i => i.name.trim() === nameToSelect.trim());
      history.push(`/automation/integration/${item.type}/${item.id}`);
    }

    return [
      getIntegrationsFulfilled(integrations)
    ];
  }
};

export const EpicGetIntegrations: Epic<any, any> = EpicUtils.Create(request);
