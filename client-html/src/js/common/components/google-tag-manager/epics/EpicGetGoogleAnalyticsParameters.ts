/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import { GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED, GET_TAG_MANAGER_PARAMETERS } from "../actions";
import UserPreferenceService from "../../../services/UserPreferenceService";
import { GET_USER_PREFERENCES_FULFILLED } from "../../../actions";
import {
  GOOGLE_ANALYTICS_CLIENT_ID_KEY,
  GOOGLE_ANALYTICS_COMPAIN_ID_KEY,
  GOOGLE_ANALYTICS_COMPAIN_NAME_KEY,
  GOOGLE_ANALYTICS_USER_ID_KEY
} from "../../../../constants/Config";

const request: EpicUtils.Request<{ [key: string]: string }, any> = {
  type: GET_TAG_MANAGER_PARAMETERS,
  getData: () =>
    UserPreferenceService.getUserPreferencesByKeys([
      GOOGLE_ANALYTICS_CLIENT_ID_KEY,
      GOOGLE_ANALYTICS_COMPAIN_ID_KEY,
      GOOGLE_ANALYTICS_COMPAIN_NAME_KEY,
      GOOGLE_ANALYTICS_USER_ID_KEY
    ]),
  processData: preferences => [
      {
        type: GET_USER_PREFERENCES_FULFILLED,
        payload: { preferences }
      },
      {
        type: GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED,
        payload: preferences
      }
    ]
};

export const EpicGetGoogleAnalyticsParameters: Epic<any, any> = EpicUtils.Create(request);
