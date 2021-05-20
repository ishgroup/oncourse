/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { DELETE_COURSE_CLASS_ASSESSMENT } from "../actions";
import CourseClassAssessmentService from "../services/CourseClassAssessmentService";

const request: EpicUtils.Request<any, number> = {
  type: DELETE_COURSE_CLASS_ASSESSMENT,
  getData: id => CourseClassAssessmentService.deleteCourseClassAssessment(id),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to delete class assessment")
};

export const EpicDeleteCourseClassAssessment: Epic<any, any> = EpicUtils.Create(request);
