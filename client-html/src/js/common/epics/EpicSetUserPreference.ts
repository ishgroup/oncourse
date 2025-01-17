/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { UserPreference } from "@api/model";
import { Epic } from "redux-observable";
import { getUserPreferences, SET_USER_PREFERENCE } from "../actions";
import UserPreferenceService from "../services/UserPreferenceService";
import * as EpicUtils from "./EpicUtils";

const request: EpicUtils.Request<any, UserPreference> = {
  type: SET_USER_PREFERENCE,
  getData: userPreference => UserPreferenceService.setUserPreferenceByKey(userPreference),
  processData: (v, s, userPreference) => [getUserPreferences([userPreference.key])],
};

export const EpicSetUserPreference: Epic<any, any> = EpicUtils.Create(request);
