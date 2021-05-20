/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Session, SessionWarning } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_COURSE_CLASS_SESSIONS_WARNINGS, setCourseClassSessionsWarnings } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import CourseClassTimetableService from "../components/timetable/services/CourseClassTimetableService";

const request: EpicUtils.Request<SessionWarning[], {classId: number, sessions: Session[]}> = {
  type: GET_COURSE_CLASS_SESSIONS_WARNINGS,
  hideLoadIndicator: true,
  getData: ({ classId, sessions }) => CourseClassTimetableService.validateUpdate(classId, sessions),
  processData: warnings => [setCourseClassSessionsWarnings(warnings)],
  processError: response => {
    if (response && response.status === 403) {
      return [];
    }
    return FetchErrorHandler(response, "Failed to get course class session warnings");
  }
};

export const EpicGetCourseClassSessionsWarnings: Epic<any, any> = EpicUtils.Create(request);
