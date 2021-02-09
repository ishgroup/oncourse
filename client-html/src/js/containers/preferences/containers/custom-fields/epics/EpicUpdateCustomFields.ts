/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { CustomFieldType } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { SHOW_MESSAGE } from "../../../../../common/actions";
import { UPDATE_CUSTOM_FIELDS_FULFILLED, UPDATE_CUSTOM_FIELDS_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, any, any> = {
  type: UPDATE_CUSTOM_FIELDS_REQUEST,
  getData: payload => {
    payload.customFields.forEach(cc => {
      delete cc.uniqid;
    });

    return PreferencesService.updateCustomFields(payload.customFields);
  },
  retrieveData: () => PreferencesService.getCustomFields(),
  processData: (items: CustomFieldType[]) => [
      {
        type: UPDATE_CUSTOM_FIELDS_FULFILLED,
        payload: { customFields: items }
      },
      {
        type: SHOW_MESSAGE,
        payload: { message: "Custom fields were successfully updated", success: true }
      }
    ],
  processError: response => FetchErrorHandler(response, "Error. Custom fields were not updated")
};

export const EpicUpdateCustomFields: Epic<any, any> = EpicUtils.Create(request);
