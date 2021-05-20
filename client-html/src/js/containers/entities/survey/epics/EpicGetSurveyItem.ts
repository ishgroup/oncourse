/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_STUDENT_SURVEY_ITEM, GET_STUDENT_SURVEY_ITEM_FULFILLED } from "../actions/index";
import { SurveyItem } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import SurveyService from "../services/SurveyService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_STUDENT_SURVEY_ITEM,
  getData: (id: number) => SurveyService.getSurveyItem(id),
  processData: (surveyItem: SurveyItem) => {
    return [
      {
        type: GET_STUDENT_SURVEY_ITEM_FULFILLED,
        payload: { surveyItem }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: surveyItem, name: surveyItem.studentName + " - " + surveyItem.className }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, surveyItem)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetSurveyItem: Epic<any, any> = EpicUtils.Create(request);
