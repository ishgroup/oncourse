/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { DELETE_COURSE_CLASS_TUTOR } from "../actions";
import CourseClassTutorService from "../services/CourseClassTutorService";

const request: EpicUtils.Request<any, number> = {
  type: DELETE_COURSE_CLASS_TUTOR,
  getData: id => CourseClassTutorService.deleteCourseClassTutor(id),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to delte course class tutor")
};

export const EpicDeleteCourseClassTutor: Epic<any, any> = EpicUtils.Create(request);
