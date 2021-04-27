/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../common/actions";
import PreferencesService from "../services/PreferencesService";
import { SAVE_PREFERENCES_FULFILLED, SAVE_PREFERENCES_REQUEST } from "../actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: SAVE_PREFERENCES_REQUEST,
  getData: payload => PreferencesService.savePreferences(payload.fields),
  retrieveData: payload => PreferencesService.getPreferences(payload.category),
  processData: (preferences: any, state: any, payload) => {
    return [
      {
        type: SAVE_PREFERENCES_FULFILLED,
        payload: { preferences, category: payload.category }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Form was successfully saved" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Form was not saved");
  }
};

export const EpicSavePreferences: Epic<any, any> = EpicUtils.Create(request);
