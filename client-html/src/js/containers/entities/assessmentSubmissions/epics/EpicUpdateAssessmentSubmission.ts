/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { AssessmentSubmission } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {
  GET_ASSESSMENT_SUBMISSIONS_ITEM,
  UPDATE_ASSESSMENT_SUBMISSIONS_ITEM,
  UPDATE_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED
} from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import AssessmentSubmissionService from "../service/AssessmentSubmissionService";

const request: EpicUtils.Request<any, any, { id: number; assessmentSubmission: AssessmentSubmission & { notes: any } }> = {
  type: UPDATE_ASSESSMENT_SUBMISSIONS_ITEM,
  getData: ({ id, assessmentSubmission }) => {
    delete assessmentSubmission.notes;
    return AssessmentSubmissionService.updateAssessmentSubmission(id, assessmentSubmission);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
    {
      type: UPDATE_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Assessment Submission Record updated" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "AssessmentSubmission", listUpdate: true, savedID: id }
    },
    {
      type: GET_ASSESSMENT_SUBMISSIONS_ITEM,
      payload: id
    }
  ],
  processError: (response, { assessmentSubmission }) => (
    [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, assessmentSubmission)]
  )
};

export const EpicUpdateAssessmentSubmissionItem: Epic<any, any> = EpicUtils.Create(request);