/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClass, PermissionRequest } from '@api/model';
import { initialize } from 'redux-form';
import { Epic } from 'redux-observable';
import { checkPermissionsRequestFulfilled, clearActionsQueue } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { getNoteItems } from '../../../../common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import AccessService from '../../../../common/services/AccessService';
import { courseClassBudgetPath, plainEnrolmentPath } from '../../../../constants/Api';
import { CourseClassExtended } from '../../../../model/entities/CourseClass';
import { getEntityItemById } from '../../common/entityItemsService';
import { GET_COURSE_CLASS, GET_COURSE_CLASS_ENROLMENTS } from '../actions';
import { GET_COURSE_CLASS_ASSESSMENTS } from '../components/assessments/actions';
import { GET_COURSE_CLASS_ATTENDANCE } from '../components/attendance/actions';
import { GET_COURSE_CLASS_COSTS } from '../components/budget/actions';
import { GET_COURSE_CLASS_SESSIONS } from '../components/timetable/actions';
import { GET_COURSE_CLASS_TUTORS } from '../components/tutors/actions';

const budgetAccessRequest: PermissionRequest = { path: courseClassBudgetPath, method: "GET" };
const enrolmentAccessRequest: PermissionRequest = { path: plainEnrolmentPath, method: "GET" };

const request: EpicUtils.Request<{  courseClass: CourseClass, budgetAccess: boolean, enrolmentAccess: boolean }, number> = {
  type: GET_COURSE_CLASS,
  hideLoadIndicator: true,
  getData: async id => {
    const courseClass = await getEntityItemById("CourseClass", id);
    const budgetAccess = await AccessService.checkPermissions(budgetAccessRequest);
    const enrolmentAccess = await AccessService.checkPermissions(enrolmentAccessRequest);
    
    return { courseClass, budgetAccess: budgetAccess.hasAccess, enrolmentAccess: enrolmentAccess.hasAccess };
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
    ];

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
      checkPermissionsRequestFulfilled({
        ...budgetAccessRequest,
        hasAccess: budgetAccess
      }),
      checkPermissionsRequestFulfilled({
        ...enrolmentAccessRequest,
        hasAccess: enrolmentAccess
      }),
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