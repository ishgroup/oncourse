/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { CustomFieldType } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { DELETE_CUSTOM_FIELD_FULFILLED, DELETE_CUSTOM_FIELD_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, any, any> = {
  type: DELETE_CUSTOM_FIELD_REQUEST,
  getData: payload => PreferencesService.deleteCustomFields(payload.id),
  retrieveData: () => PreferencesService.getCustomFields(),
  processData: (items: CustomFieldType[]) => [
      {
        type: DELETE_CUSTOM_FIELD_FULFILLED,
        payload: { customFields: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Custom Field was successfully deleted" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Error. Custom Field was not deleted")
};

export const EpicDeleteCustomField: Epic<any, any> = EpicUtils.Create(request);
