/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_DATA_COLLECTION_FORMS_FULFILLED, GET_DATA_COLLECTION_FORMS_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_DATA_COLLECTION_FORMS_REQUEST,
  getData: () => PreferencesService.getDataCollectionForms(),
  processData: dataCollectionForms => {
    dataCollectionForms.sort((a, b) => a.name > b.name ? 1 : -1);
    return [
      {
        type: GET_DATA_COLLECTION_FORMS_FULFILLED,
        payload: { dataCollectionForms }
      }
    ];
  }
};

export const EpicGetDataCollectionForms: Epic<any, any> = EpicUtils.Create(request);
