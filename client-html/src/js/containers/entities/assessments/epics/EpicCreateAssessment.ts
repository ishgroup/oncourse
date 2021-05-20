/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Assessment } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import AssessmentService from "../services/AssessmentService";
import { CREATE_ASSESSMENT_ITEM, CREATE_ASSESSMENT_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Assessment;

const request: EpicUtils.Request = {
  type: CREATE_ASSESSMENT_ITEM,
  getData: payload => {
    savedItem = payload.assessment;
    return AssessmentService.createAssessment(payload.assessment);
  },
  processData: () => [
      {
        type: CREATE_ASSESSMENT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Assessment Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Assessment" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: response => [
    ...FetchErrorHandler(response, "Assessment Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateAssessment: Epic<any, any> = EpicUtils.Create(request);
