/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../epics/EpicUtils";
import { GET_USER_PREFERENCES, GET_USER_PREFERENCES_FULFILLED } from "../actions";
import { PreferenceEnum } from "@api/model";
import UserPreferenceService from "../services/UserPreferenceService";

const request: EpicUtils.Request<{ [key: string]: string }, any, PreferenceEnum[]> = {
  type: GET_USER_PREFERENCES,
  getData: keys => UserPreferenceService.getUserPreferencesByKeys(keys),
  processData: preferences => {
    return [
      {
        type: GET_USER_PREFERENCES_FULFILLED,
        payload: { preferences }
      }
    ];
  }
};

export const EpicGetUserPreferences: Epic<any, any> = EpicUtils.Create(request);
