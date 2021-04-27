/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Assessment } from "@api/model";
import { initialize } from "redux-form";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ASSESSMENT_ITEM, GET_ASSESSMENT_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import AssessmentService from "../services/AssessmentService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_ASSESSMENT_ITEM,
  getData: (id: number) => AssessmentService.getAssessment(id),
  processData: (assessment: Assessment, s, id) => [
      {
        type: GET_ASSESSMENT_ITEM_FULFILLED,
        payload: { assessment }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: assessment, name: assessment.name }
      },
    getNoteItems("Assessment", id, LIST_EDIT_VIEW_FORM_NAME),
    initialize(LIST_EDIT_VIEW_FORM_NAME, assessment)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetAssessment: Epic<any, any> = EpicUtils.Create(request);
