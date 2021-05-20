/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { CREATE_DATA_COLLECTION_FORM_FULFILLED, CREATE_DATA_COLLECTION_FORM_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: CREATE_DATA_COLLECTION_FORM_REQUEST,
  getData: payload => PreferencesService.createDataCollectionForm(payload.form),
  retrieveData: () => PreferencesService.getDataCollectionForms(),
  processData: (dataCollectionForms: any) => {
    return [
      {
        type: CREATE_DATA_COLLECTION_FORM_FULFILLED,
        payload: { dataCollectionForms }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New form was successfully created" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Form was not created");
  }
};

export const EpicCreateDataCollectionForm: Epic<any, any> = EpicUtils.Create(request);
