/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Session } from "@api/model";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { POST_COURSE_CLASS_SESSIONS } from "../actions";
import CourseClassTimetableService from "../services/CourseClassTimetableService";

const request: EpicUtils.Request<any, { classId: number; sessions: Session[] }> = {
  type: POST_COURSE_CLASS_SESSIONS,
  hideLoadIndicator: true,
  getData: ({ classId, sessions }) =>
    CourseClassTimetableService.updateTimetableSessionsForCourseClass(classId, sessions),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to update class sessions")
};

export const EpicPostCourseClassSessions: Epic<any, any> = EpicUtils.Create(request);
