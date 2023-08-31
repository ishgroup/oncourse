/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentClass } from "@api/model";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { UPDATE_COURSE_CLASS_ASSESSMENT } from "../actions";
import CourseClassAssessmentService from "../services/CourseClassAssessmentService";

const request: EpicUtils.Request<any, { assessment: AssessmentClass }> = {
  type: UPDATE_COURSE_CLASS_ASSESSMENT,
  getData: ({ assessment }) => CourseClassAssessmentService.updateCourseClassAssessment(assessment),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to update class assessment")
};

export const EpicUpdateCourseClassAssessment: Epic<any, any> = EpicUtils.Create(request);
