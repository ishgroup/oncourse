/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import PreferencesService from "../services/PreferencesService";
import { GET_PREFERENCES_BY_KEYS_FULFILLED, GET_PREFERENCES_BY_KEYS_REQUEST } from "../actions";
import { Categories } from "../../../model/preferences";

const request: EpicUtils.Request<any, { keys: string[]; category: Categories }> = {
  type: GET_PREFERENCES_BY_KEYS_REQUEST,
  getData: payload => PreferencesService.getPreferenceByKeys(payload.keys),
  processData: (preferences, s, { category }) => {
    return [
      {
        type: GET_PREFERENCES_BY_KEYS_FULFILLED,
        payload: { preferences, category }
      }
    ];
  }
};

export const EpicGetPreferencesByKeys: Epic<any, any> = EpicUtils.Create(request);
