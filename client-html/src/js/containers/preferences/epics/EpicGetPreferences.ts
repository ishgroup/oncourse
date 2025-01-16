/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_PREFERENCES_FULFILLED, GET_PREFERENCES_REQUEST } from "../actions";
import PreferencesService from "../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_PREFERENCES_REQUEST,
  getData: payload => PreferencesService.getPreferences(payload),
  processData: (preferences: any, state: any, payload) => [
    {
      type: GET_PREFERENCES_FULFILLED,
      payload: { preferences, category: payload }
    }
  ]
};

export const EpicGetPreferences: Epic<any, any> = EpicUtils.Create(request);
