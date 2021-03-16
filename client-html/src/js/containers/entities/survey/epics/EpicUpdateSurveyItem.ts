/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { SurveyItem } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_STUDENT_SURVEY_ITEM, UPDATE_SURVEY_ITEM, UPDATE_SURVEY_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; survey: SurveyItem }> = {
  type: UPDATE_SURVEY_ITEM,
  getData: ({ id, survey }) => {
    processCustomFields(survey);
    return updateEntityItemById("Survey", id, survey);
  },
  processData: (v, s, { id }) => [
      {
        type: UPDATE_SURVEY_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Student Feedback Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Survey", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_STUDENT_SURVEY_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { survey }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, survey)]
};

export const EpicUpdateSurveyItem: Epic<any, any> = EpicUtils.Create(request);
