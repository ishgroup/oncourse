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
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ASSESSMENT_SUBMISSIONS_ITEM, GET_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED } from "../actions";
import AssessmentService from "../../assessments/services/AssessmentService";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getNoteItems } from "../../../../common/components/form/notes/actions";

const request: EpicUtils.Request<any, any, any> = {
  type: GET_ASSESSMENT_SUBMISSIONS_ITEM,
  getData: (id: number) => AssessmentService.getAssessment(id),
  processData: (assessmentSubmission: AssessmentSubmission, s, id) => [
    {
      type: GET_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED,
      payload: { assessmentSubmission }
    },
    {
      type: SET_LIST_EDIT_RECORD,
      payload: { editRecord: assessmentSubmission, name: assessmentSubmission.id }
    },
    getNoteItems("AssessmentSubmission", id, LIST_EDIT_VIEW_FORM_NAME),
    initialize(LIST_EDIT_VIEW_FORM_NAME, assessmentSubmission)
  ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetAssessmentSubmissionItem: Epic<any, any> = EpicUtils.Create(request);