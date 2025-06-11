/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClass, PermissionRequest } from '@api/model';
import { initialize } from 'redux-form';
import { Epic } from 'redux-observable';
import { clearActionsQueue } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { getNoteItems } from '../../../../common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import { courseClassBudgetPath, plainEnrolmentPath } from '../../../../constants/Api';
import { AccessByPath } from '../../../../model/entities/common';
import { CourseClassExtended } from '../../../../model/entities/CourseClass';
import { getEntityItemById } from '../../common/entityItemsService';
import { getAccessesByPath } from '../../common/utils';
import { GET_COURSE_CLASS, GET_COURSE_CLASS_ENROLMENTS } from '../actions';
import { GET_COURSE_CLASS_ASSESSMENTS } from '../components/assessments/actions';
import { GET_COURSE_CLASS_ATTENDANCE } from '../components/attendance/actions';
import { GET_COURSE_CLASS_COSTS } from '../components/budget/actions';
import { GET_COURSE_CLASS_SESSIONS } from '../components/timetable/actions';
import { GET_COURSE_CLASS_TUTORS } from '../components/tutors/actions';

const request: EpicUtils.Request<{  courseClass: CourseClass, budgetAccess: AccessByPath, enrolmentAccess: AccessByPath }, number> = {
  type: GET_COURSE_CLASS,
  hideLoadIndicator: true,
  getData: async (id, state) => {
    const courseClass = await getEntityItemById("CourseClass", id);

    const [
      budgetAccess,
      enrolmentAccess,
    ] = await getAccessesByPath(
      [
        courseClassBudgetPath,
        plainEnrolmentPath
      ],
      state
    );

    return { courseClass, budgetAccess, enrolmentAccess };
  },
  processData: ({ courseClass, budgetAccess, enrolmentAccess }, s, id) => {
    const courseClassEx: CourseClassExtended = { ...courseClass };
    courseClassEx.tutors = [];
    courseClassEx.sessions = [];
    courseClassEx.assessments = [];
    courseClassEx.studentAttendance = [];
    courseClassEx.trainingPlan = [];
    courseClassEx.budget = [];
    courseClassEx.notes = [];

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
      ...[
        budgetAccess,
        enrolmentAccess
      ].filter(p => p.action).map(p => p.action)
    ];

    if (budgetAccess.hasAccess) {
      relatedActions.push({
        type: GET_COURSE_CLASS_COSTS,
        payload: id
      });
    }

    if (enrolmentAccess.hasAccess) {
      relatedActions.push({
        type: GET_COURSE_CLASS_ENROLMENTS,
        payload: id
      });
    }

    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: {
          editRecord: courseClassEx,
          name: `${courseClassEx.courseName} ${courseClassEx.courseCode}-${courseClassEx.code}`
        }
      },
      ...relatedActions,
      initialize(LIST_EDIT_VIEW_FORM_NAME, courseClassEx),
      getNoteItems("CourseClass", id, LIST_EDIT_VIEW_FORM_NAME),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetCourseClass: Epic<any, any> = EpicUtils.Create(request);