/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { DELETE_CUSTOM_FIELD_REQUEST, getCustomFields } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { showMessage } from "../../../../../common/actions";

const request: EpicUtils.Request<any, any> = {
  type: DELETE_CUSTOM_FIELD_REQUEST,
  getData: payload => PreferencesService.deleteCustomFields(payload.id),
  processData: () => [
    showMessage({
      message: "Custom field was successfully deleted",
      success: true
    }),
    getCustomFields()
  ],
  processError: response => FetchErrorHandler(response, "Error. Custom field was not deleted")
};

export const EpicDeleteCustomField: Epic<any, any> = EpicUtils.Create(request);
