/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Assessment } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ASSESSMENT_ITEM, UPDATE_ASSESSMENT_ITEM, UPDATE_ASSESSMENT_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import AssessmentService from "../services/AssessmentService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; assessment: Assessment & { notes: any } }> = {
  type: UPDATE_ASSESSMENT_ITEM,
  getData: ({ id, assessment }) => {
    delete assessment.notes;
    return AssessmentService.updateAssessment(id, assessment);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_ASSESSMENT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Assessment Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Assessment", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_ASSESSMENT_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { assessment }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, assessment)]
};

export const EpicUpdateAssessmentItem: Epic<any, any> = EpicUtils.Create(request);
