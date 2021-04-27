/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import PreferencesService from "../services/PreferencesService";
import { GET_COLUMNS_WIDTH_REQUEST, GET_COLUMNS_WIDTH_REQUEST_FULFILLED } from "../actions";
import { ColumnWidth } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_COLUMNS_WIDTH_REQUEST,
  getData: () => PreferencesService.getColumnsWidth(),
  processData: (columnWidth: ColumnWidth) => {
    return [
      {
        type: GET_COLUMNS_WIDTH_REQUEST_FULFILLED,
        payload: { columnWidth }
      }
    ];
  }
};

export const EpicGetColumnsWidth: Epic<any, any> = EpicUtils.Create(request);
