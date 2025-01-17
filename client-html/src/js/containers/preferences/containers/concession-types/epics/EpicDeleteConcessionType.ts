/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ConcessionType } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { DELETE_CONCESSION_TYPE_FULFILLED, DELETE_CONCESSION_TYPE_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: DELETE_CONCESSION_TYPE_REQUEST,
  getData: payload => PreferencesService.deleteConcessionType(payload.id),
  retrieveData: () => PreferencesService.getConcessionTypes(),
  processData: (items: ConcessionType[]) => {
    return [
      {
        type: DELETE_CONCESSION_TYPE_FULFILLED,
        payload: { concessionTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Concession Type was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Concession Type was not deleted");
  }
};

export const EpicDeleteConcessionType: Epic<any, any> = EpicUtils.Create(request);
