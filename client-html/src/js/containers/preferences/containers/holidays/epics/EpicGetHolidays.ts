/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_HOLIDAYS_FULFILLED, GET_HOLIDAYS_REQUEST } from "../../../actions";

const request: EpicUtils.Request = {
  type: GET_HOLIDAYS_REQUEST,
  getData: () => PreferencesService.getHolidays(),
  processData: (holidays: any) => {
    return [
      {
        type: GET_HOLIDAYS_FULFILLED,
        payload: { holidays }
      }
    ];
  }
};

export const EpicGetHolidays: Epic<any, any> = EpicUtils.Create(request);
