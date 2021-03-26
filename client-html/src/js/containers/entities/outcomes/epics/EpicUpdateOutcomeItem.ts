/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Outcome } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_OUTCOME_ITEM, UPDATE_OUTCOME_ITEM } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import OutcomeService from "../services/OutcomeService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; outcome: Outcome }> = {
  type: UPDATE_OUTCOME_ITEM,
  getData: ({ id, outcome }) => OutcomeService.updateOutcome(id, outcome),
  processData: (v, s, { id }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Outcome Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Outcome", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_OUTCOME_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { outcome }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, outcome)]
};

export const EpicUpdateOutcomeItem: Epic<any, any> = EpicUtils.Create(request);
