/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { SAVE_HOLIDAYS_FULFILLED, SAVE_HOLIDAYS_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: SAVE_HOLIDAYS_REQUEST,
  getData: payload => PreferencesService.saveHolidays(payload.items),
  retrieveData: () => PreferencesService.getHolidays(),
  processData: items => {
    return [
      {
        type: SAVE_HOLIDAYS_FULFILLED,
        payload: { holidays: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Form was successfully saved" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Form was not saved");
  }
};

export const EpicSaveHolidays: Epic<any, any> = EpicUtils.Create(request);
