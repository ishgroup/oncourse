/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SystemPreference } from "@api/model";
import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_USI_SORTWARE_ID, GET_USI_SORTWARE_ID_FULFILLED } from "../actions";
import PreferencesService from "../services/PreferencesService";

const request: EpicUtils.Request<SystemPreference[]> = {
  type: GET_USI_SORTWARE_ID,
  getData: () => {
    return PreferencesService.getUSISoftwareId();
  },
  processData: (usiId: SystemPreference[]) => {
    return [
      {
        type: GET_USI_SORTWARE_ID_FULFILLED,
        payload: usiId[0].valueString
      }
    ];
  }
};

export const EpicGetUSISortwareId: Epic<any, any> = EpicUtils.Create(request);
