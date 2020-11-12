/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { State } from "../../../../../../reducers/state";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { POST_COURSE_CLASS_TUTOR } from "../actions";
import CourseClassTutorService from "../services/CourseClassTutorService";
import { CourseClassTutor } from "@api/model";

const request: EpicUtils.Request<any, State, { tutor: CourseClassTutor }> = {
  type: POST_COURSE_CLASS_TUTOR,
  getData: ({ tutor }) => CourseClassTutorService.postCourseClassTutor(tutor),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to create course class tutor")
};

export const EpicPostCourseClassTutor: Epic<any, any> = EpicUtils.Create(request);
