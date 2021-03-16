/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import PreferencesService from "../services/PreferencesService";
import { UPDATE_COLUMNS_WIDTH_REQUEST, UPDATE_COLUMNS_WIDTH_REQUEST_FULFILLED } from "../actions";
import { ColumnWidth } from "@api/model";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: UPDATE_COLUMNS_WIDTH_REQUEST,
  getData: (columnWidth: ColumnWidth) => PreferencesService.updateColumnsWidth(columnWidth),
  processData: () => {
    return [
      {
        type: UPDATE_COLUMNS_WIDTH_REQUEST_FULFILLED
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Error. Column size was not saved")
};

export const EpicUpdateColumnsWidth: Epic<any, any> = EpicUtils.Create(request);
