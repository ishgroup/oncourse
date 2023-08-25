/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor } from "@api/model";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { PUT_COURSE_CLASS_TUTOR } from "../actions";
import CourseClassTutorService from "../services/CourseClassTutorService";

const request: EpicUtils.Request<any, { tutor: CourseClassTutor }> = {
  type: PUT_COURSE_CLASS_TUTOR,
  getData: ({ tutor }) => CourseClassTutorService.putCourseClassTutor(tutor),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to update course class tutor")
};

export const EpicPutCourseClassTutor: Epic<any, any> = EpicUtils.Create(request);
