/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { AssessmentClass } from "@api/model";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { CREATE_COURSE_CLASS_ASSESSMENT } from "../actions";
import CourseClassAssessmentService from "../services/CourseClassAssessmentService";

const request: EpicUtils.Request<any, { assessment: AssessmentClass }> = {
  type: CREATE_COURSE_CLASS_ASSESSMENT,
  getData: ({ assessment }) => CourseClassAssessmentService.createCourseClassAssessment(assessment),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to create class assessment")
};

export const EpicCreateCourseClassAssessment: Epic<any, any> = EpicUtils.Create(request);
