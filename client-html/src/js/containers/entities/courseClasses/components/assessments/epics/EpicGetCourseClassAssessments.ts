/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../../common/components/list-view/constants";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_COURSE_CLASS_ASSESSMENTS } from "../actions";
import CourseClassAssessmentService from "../services/CourseClassAssessmentService";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE_CLASS_ASSESSMENTS,
  hideLoadIndicator: true,
  getData: id => CourseClassAssessmentService.getCourseClassAssessments(id),
  processData: (assessments, s, id) => {
    if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
      return [];
    }

    assessments.forEach((a, index) => {
      a.index = index;
    });

    return [initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, assessments })];
  },
  processError: response => FetchErrorHandler(response, "Failed to get class assessments")
};

export const EpicGetCourseClassAssessments: Epic<any, any> = EpicUtils.Create(request);
