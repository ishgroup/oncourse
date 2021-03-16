/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_DATA_COLLECTION_FORMS_FULFILLED, GET_DATA_COLLECTION_FORMS_REQUEST } from "../../../actions";

const request: EpicUtils.Request = {
  type: GET_DATA_COLLECTION_FORMS_REQUEST,
  getData: () => PreferencesService.getDataCollectionForms(),
  processData: dataCollectionForms => {
    return [
      {
        type: GET_DATA_COLLECTION_FORMS_FULFILLED,
        payload: { dataCollectionForms }
      }
    ];
  }
};

export const EpicGetDataCollectionForms: Epic<any, any> = EpicUtils.Create(request);
