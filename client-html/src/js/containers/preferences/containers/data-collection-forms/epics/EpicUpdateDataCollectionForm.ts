/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import {
  GET_DATA_COLLECTION_RULES_REQUEST,
  UPDATE_DATA_COLLECTION_FORM_FULFILLED,
  UPDATE_DATA_COLLECTION_FORM_REQUEST
} from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: UPDATE_DATA_COLLECTION_FORM_REQUEST,
  getData: payload => PreferencesService.updateDataCollectionForm(payload.id, payload.form),
  retrieveData: () => PreferencesService.getDataCollectionForms(),
  processData: (dataCollectionForms: any) => {
    return [
      {
        type: UPDATE_DATA_COLLECTION_FORM_FULFILLED,
        payload: { dataCollectionForms }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Form was successfully updated" }
      },
      // Update Data Collection Rules depending values
      {
        type: GET_DATA_COLLECTION_RULES_REQUEST
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Form was not updated");
  }
};

export const EpicUpdateDataCollectionForm: Epic<any, any> = EpicUtils.Create(request);
