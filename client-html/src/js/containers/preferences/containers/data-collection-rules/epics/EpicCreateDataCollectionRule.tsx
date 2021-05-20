/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { CREATE_DATA_COLLECTION_RULE_FULFILLED, CREATE_DATA_COLLECTION_RULE_REQUEST } from "../../../actions";
import { DataCollectionRule } from "@api/model";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: CREATE_DATA_COLLECTION_RULE_REQUEST,
  getData: payload => PreferencesService.createDataCollectionRule(payload.rule),
  retrieveData: () => PreferencesService.getDataCollectionRules(),
  processData: (dataCollectionRules: DataCollectionRule[]) => {
    return [
      {
        type: CREATE_DATA_COLLECTION_RULE_FULFILLED,
        payload: { dataCollectionRules }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New rule was successfully created" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Rule was not created");
  }
};

export const EpicCreateDataCollectionRule: Epic<any, any> = EpicUtils.Create(request);
