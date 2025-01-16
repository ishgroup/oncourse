/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { DELETE_DATA_COLLECTION_FORM_FULFILLED, DELETE_DATA_COLLECTION_FORM_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: DELETE_DATA_COLLECTION_FORM_REQUEST,
  getData: payload => PreferencesService.deleteDataCollectionForm(payload.id),
  retrieveData: () => PreferencesService.getDataCollectionForms(),
  processData: (dataCollectionForms: any) => {
    dataCollectionForms.sort((a, b) => a.name > b.name ? 1 : -1);
    return [
      {
        type: DELETE_DATA_COLLECTION_FORM_FULFILLED,
        payload: { dataCollectionForms }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Form was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Form was not deleted");
  }
};

export const EpicDeleteDataCollectionForm: Epic<any, any> = EpicUtils.Create(request);
