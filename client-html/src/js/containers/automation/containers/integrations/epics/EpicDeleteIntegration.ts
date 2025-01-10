/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import history from "../../../../../constants/History";
import { DELETE_INTEGRATION_ITEM_REQUEST, getIntegrations } from "../../../actions";
import IntegrationService from "../services";

const request: EpicUtils.Request = {
  type: DELETE_INTEGRATION_ITEM_REQUEST,
  getData: payload => IntegrationService.deleteIntegrationItem(payload.id),
  processData: () => {
    history.push('/automation/integrations');
    return [
      getIntegrations(),
      {
        type: FETCH_SUCCESS,
        payload: { message: "Integration was successfully deleted" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Error. Integration was not deleted")
};

export const EpicDeleteIntegration: Epic<any, any> = EpicUtils.Create(request);
