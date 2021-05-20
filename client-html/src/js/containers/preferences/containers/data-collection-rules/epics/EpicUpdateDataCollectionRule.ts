/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { UPDATE_DATA_COLLECTION_RULE_FULFILLED, UPDATE_DATA_COLLECTION_RULE_REQUEST } from "../../../actions";
import { DataCollectionRule } from "@api/model";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: UPDATE_DATA_COLLECTION_RULE_REQUEST,
  getData: payload => PreferencesService.updateDataCollectionRule(payload.id, payload.rule),
  retrieveData: () => PreferencesService.getDataCollectionRules(),
  processData: (dataCollectionRules: DataCollectionRule[]) => {
    return [
      {
        type: UPDATE_DATA_COLLECTION_RULE_FULFILLED,
        payload: { dataCollectionRules }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Rule was successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Rule was not updated");
  }
};

export const EpicUpdateDataCollectionRule: Epic<any, any> = EpicUtils.Create(request);
