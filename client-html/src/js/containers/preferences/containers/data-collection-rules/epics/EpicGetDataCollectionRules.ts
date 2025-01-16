/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataCollectionRule } from "@api/model";
import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_DATA_COLLECTION_RULES_FULFILLED, GET_DATA_COLLECTION_RULES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_DATA_COLLECTION_RULES_REQUEST,
  getData: () => PreferencesService.getDataCollectionRules(),
  processData: (dataCollectionRules: DataCollectionRule[]) => {
    dataCollectionRules.sort((a, b) => a.name > b.name ? 1 : -1);
    return [
      {
        type: GET_DATA_COLLECTION_RULES_FULFILLED,
        payload: { dataCollectionRules }
      }
    ];
  }
};

export const EpicGetDataCollectionRules: Epic<any, any> = EpicUtils.Create(request);
