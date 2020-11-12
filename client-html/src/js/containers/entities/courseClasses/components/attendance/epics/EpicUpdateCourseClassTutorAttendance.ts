/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { State } from "../../../../../../reducers/state";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Epic } from "redux-observable";
import { UPDATE_COURSE_CLASS_TUTOR_ATTENDANCE } from "../actions";
import CourseClassAttendanceService from "../services/CourseClassAttendanceService";
import { TutorAttendance } from "@api/model";

const request: EpicUtils.Request<any, State, { id: number; tutorAttendance: TutorAttendance[] }> = {
  type: UPDATE_COURSE_CLASS_TUTOR_ATTENDANCE,
  getData: ({ id, tutorAttendance }) => CourseClassAttendanceService.updateTutorAttendance(id, tutorAttendance),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to update tutor attendance")
};

export const EpicUpdateCourseClassTutorAttendance: Epic<any, any> = EpicUtils.Create(request);
