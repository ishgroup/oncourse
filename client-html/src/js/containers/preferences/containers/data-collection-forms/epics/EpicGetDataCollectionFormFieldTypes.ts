/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import {
  GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED,
  GET_DATA_COLLECTION_FORM_FIELD_TYPES_REQUEST
} from "../../../actions";
import { FieldType } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_DATA_COLLECTION_FORM_FIELD_TYPES_REQUEST,
  getData: payload => PreferencesService.getDataCollectionFormFieldTypes(payload.formType),
  processData: (dataCollectionFormFieldTypes: FieldType[]) => {
    return [
      {
        type: GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED,
        payload: { dataCollectionFormFieldTypes }
      }
    ];
  }
};

export const EpicGetDataCollectionFormFieldTypes: Epic<any, any> = EpicUtils.Create(request);
