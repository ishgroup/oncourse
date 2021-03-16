/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { CourseClassExtended } from "../../../../model/entities/CourseClass";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { courseClassBudgetPath, plainEnrolmentPath } from "../../../../constants/Api";
import { GET_COURSE_CLASS, GET_COURSE_CLASS_ENROLMENTS } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { GET_COURSE_CLASS_TUTORS } from "../components/tutors/actions";
import { GET_COURSE_CLASS_COSTS } from "../components/budget/actions";
import { GET_COURSE_CLASS_SESSIONS } from "../components/timetable/actions";
import { GET_COURSE_CLASS_ASSESSMENTS } from "../components/assessments/actions";
import { GET_COURSE_CLASS_ATTENDANCE } from "../components/attendance/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { clearActionsQueue } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE_CLASS,
  hideLoadIndicator: true,
  getData: id => getEntityItemById("CourseClass", id),
  processData: (courseClass: CourseClassExtended, s, id) => {
    courseClass.tutors = [];
    courseClass.sessions = [];
    courseClass.assessments = [];
    courseClass.studentAttendance = [];
    courseClass.tutorAttendance = [];
    courseClass.trainingPlan = [];
    courseClass.budget = [];
    courseClass.notes = [];

    const relatedActions = [
      {
        type: GET_COURSE_CLASS_TUTORS,
        payload: id
      },
      {
        type: GET_COURSE_CLASS_ASSESSMENTS,
        payload: id
      },
      {
        type: GET_COURSE_CLASS_SESSIONS,
        payload: id
      },
      {
        type: GET_COURSE_CLASS_ATTENDANCE,
        payload: id
      },
    ];

    const budgetAccess = s.access[courseClassBudgetPath] && s.access[courseClassBudgetPath]["GET"];
    const enrolmentAccess = s.access[plainEnrolmentPath] && s.access[plainEnrolmentPath]["GET"];

    if (budgetAccess) {
      relatedActions.push({
        type: GET_COURSE_CLASS_COSTS,
        payload: id
      });
    }

    if (enrolmentAccess) {
      relatedActions.push({
        type: GET_COURSE_CLASS_ENROLMENTS,
        payload: id
      });
    }

    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: {
          editRecord: courseClass,
          name: `${courseClass.courseName} ${courseClass.courseCode}-${courseClass.code}`
        }
      },
      ...relatedActions,
      initialize(LIST_EDIT_VIEW_FORM_NAME, courseClass),
      getNoteItems("CourseClass", id, LIST_EDIT_VIEW_FORM_NAME),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetCourseClass: Epic<any, any> = EpicUtils.Create(request);
