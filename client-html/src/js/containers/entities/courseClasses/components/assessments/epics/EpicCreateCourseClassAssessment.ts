/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { State } from "../../../../../../reducers/state";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { CREATE_COURSE_CLASS_ASSESSMENT } from "../actions";
import CourseClassAssessmentService from "../services/CourseClassAssessmentService";
import { AssessmentClass } from "@api/model";

const request: EpicUtils.Request<any, State, { assessment: AssessmentClass }> = {
  type: CREATE_COURSE_CLASS_ASSESSMENT,
  getData: ({ assessment }) => CourseClassAssessmentService.createCourseClassAssessment(assessment),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to create class assessment")
};

export const EpicCreateCourseClassAssessment: Epic<any, any> = EpicUtils.Create(request);
