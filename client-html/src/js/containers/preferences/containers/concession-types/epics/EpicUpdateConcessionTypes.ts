/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { UPDATE_CONCESSION_TYPES_FULFILLED, UPDATE_CONCESSION_TYPES_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { ConcessionType } from "@api/model";

const request: EpicUtils.Request = {
  type: UPDATE_CONCESSION_TYPES_REQUEST,
  getData: payload => PreferencesService.updateConcessionTypes(payload.concessionTypes),
  retrieveData: () => PreferencesService.getConcessionTypes(),
  processData: (items: ConcessionType[]) => {
    return [
      {
        type: UPDATE_CONCESSION_TYPES_FULFILLED,
        payload: { concessionTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Concession Types were successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Concession Types was not updated");
  }
};

export const EpicUpdateConcessionTypes: Epic<any, any> = EpicUtils.Create(request);
