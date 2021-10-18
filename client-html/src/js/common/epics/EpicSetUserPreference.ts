/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { UserPreference } from "@api/model";
import * as EpicUtils from "./EpicUtils";
import { GET_USER_PREFERENCES, SET_USER_PREFERENCE } from "../actions";
import UserPreferenceService from "../services/UserPreferenceService";
import { GET_BLOG_POSTS } from "../../containers/dashboard/actions";
import { READED_NEWS } from "../../constants/Config";

const request: EpicUtils.Request<any, UserPreference> = {
  type: SET_USER_PREFERENCE,
  getData: userPreference => UserPreferenceService.setUserPreferenceByKey(userPreference),
  processData: () => [
    { type: GET_BLOG_POSTS },
    { type: GET_USER_PREFERENCES, payload: READED_NEWS },
  ],
};

export const EpicSetUserPreference: Epic<any, any> = EpicUtils.Create(request);
