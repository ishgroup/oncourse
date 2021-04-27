/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { UPDATE_TAX_TYPES_FULFILLED, UPDATE_TAX_TYPES_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Tax } from "@api/model";

const request: EpicUtils.Request = {
  type: UPDATE_TAX_TYPES_REQUEST,
  getData: payload => PreferencesService.updateTaxTypes(payload.taxTypes),
  retrieveData: () => PreferencesService.getTaxTypes(),
  processData: (items: Tax[]) => {
    return [
      {
        type: UPDATE_TAX_TYPES_FULFILLED,
        payload: { taxTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tax Types were successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Tax Types was not updated");
  }
};

export const EpicUpdateTaxTypes: Epic<any, any> = EpicUtils.Create(request);
