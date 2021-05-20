/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { DELETE_HOLIDAYS_ITEM_FULFILLED, DELETE_HOLIDAYS_ITEM_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: DELETE_HOLIDAYS_ITEM_REQUEST,
  getData: payload => PreferencesService.deleteHolidaysItem(payload.id),
  retrieveData: () => PreferencesService.getHolidays(),
  processData: items => {
    return [
      {
        type: DELETE_HOLIDAYS_ITEM_FULFILLED,
        payload: { holidays: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Item was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Item was not deleted");
  }
};

export const EpicDeleteHolidaysItem: Epic<any, any> = EpicUtils.Create(request);
