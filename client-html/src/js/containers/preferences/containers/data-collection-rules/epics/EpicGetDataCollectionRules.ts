/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_DATA_COLLECTION_RULES_FULFILLED, GET_DATA_COLLECTION_RULES_REQUEST } from "../../../actions";
import { DataCollectionRule } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_DATA_COLLECTION_RULES_REQUEST,
  getData: () => PreferencesService.getDataCollectionRules(),
  processData: (dataCollectionRules: DataCollectionRule[]) => {
    return [
      {
        type: GET_DATA_COLLECTION_RULES_FULFILLED,
        payload: { dataCollectionRules }
      }
    ];
  }
};

export const EpicGetDataCollectionRules: Epic<any, any> = EpicUtils.Create(request);
