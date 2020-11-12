/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_OUTCOME_ITEM } from "../actions/index";
import OutcomeService from "../services/OutcomeService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { initialize } from "redux-form";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: CREATE_OUTCOME_ITEM,
  getData: ({ outcome }) => OutcomeService.create(outcome),
  processData: () => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Outcome created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Outcome" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Outcome was not created")
};

export const EpicCreateOutcome: Epic<any, any> = EpicUtils.Create(request);
