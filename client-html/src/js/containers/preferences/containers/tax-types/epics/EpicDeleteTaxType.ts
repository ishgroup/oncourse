/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { DELETE_TAX_TYPE_FULFILLED, DELETE_TAX_TYPE_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Tax } from "@api/model";

const request: EpicUtils.Request = {
  type: DELETE_TAX_TYPE_REQUEST,
  getData: payload => PreferencesService.deleteTaxType(payload.id),
  retrieveData: () => PreferencesService.getTaxTypes(),
  processData: (items: Tax[]) => {
    return [
      {
        type: DELETE_TAX_TYPE_FULFILLED,
        payload: { taxTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tax Type was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Tax Type was not deleted");
  }
};

export const EpicDeleteTaxType: Epic<any, any> = EpicUtils.Create(request);
