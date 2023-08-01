/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PreferenceEnum } from "@api/model";
import { Epic } from "redux-observable";
import { GET_USER_PREFERENCES, GET_USER_PREFERENCES_FULFILLED } from "../actions";
import UserPreferenceService from "../services/UserPreferenceService";
import * as EpicUtils from "./EpicUtils";

const request: EpicUtils.Request<{ [key: string]: string }, PreferenceEnum[]> = {
  type: GET_USER_PREFERENCES,
  getData: keys => UserPreferenceService.getUserPreferencesByKeys(keys),
  processData: preferences => [
    {
      type: GET_USER_PREFERENCES_FULFILLED,
      payload: {preferences}
    }
  ]
};

export const EpicGetUserPreferences: Epic<any, any> = EpicUtils.Create(request);
