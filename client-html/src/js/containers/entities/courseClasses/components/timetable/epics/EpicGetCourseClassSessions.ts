/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Session } from "@api/model";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../../common/components/list-view/constants";
import { courseClassTimetablePath } from "../../../../../../constants/Api";
import { getCourseClassSessionsWarnings } from "../../../actions";
import { GET_COURSE_CLASS_SESSIONS } from "../actions";
import CourseClassTimetableService from "../services/CourseClassTimetableService";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE_CLASS_SESSIONS,
  hideLoadIndicator: true,
  getData: id => CourseClassTimetableService.findTimetableSessionsForCourseClasses(id),
  processData: (sessions: Session[], s, id) => {
    if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
      return [];
    }
    const sessionsMap = sessions.map(s => ({ ...s }));
    sessions.sort((a, b) => (new Date(a.start) > new Date(b.start) ? 1 : -1));

    const warningsAccess = s.access[courseClassTimetablePath] && s.access[courseClassTimetablePath]["POST"];

    return [
      initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, sessions, openedSession: {} }),
      ...warningsAccess ? [getCourseClassSessionsWarnings(id, sessionsMap)] : []
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get class sessions")
};

export const EpicGetCourseClassSessions: Epic<any, any> = EpicUtils.Create(request);
