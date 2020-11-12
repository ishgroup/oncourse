/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../epics/EpicUtils";
import { SET_USER_PREFERENCE } from "../actions";
import { UserPreference } from "@api/model";
import UserPreferenceService from "../services/UserPreferenceService";

const request: EpicUtils.Request<any, any, UserPreference> = {
  type: SET_USER_PREFERENCE,
  getData: userPreference => UserPreferenceService.setUserPreferenceByKey(userPreference),
  processData: () => []
};

export const EpicSetUserPreference: Epic<any, any> = EpicUtils.Create(request);
