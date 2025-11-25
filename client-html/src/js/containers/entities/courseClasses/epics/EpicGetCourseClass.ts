/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { initialize } from 'redux-form';
import { Epic } from 'redux-observable';
import { clearActionsQueue } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { getNoteItems } from '../../../../common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import { courseClassBudgetPath, courseClassTimetablePath, plainEnrolmentPath } from '../../../../constants/Api';
import { AccessByPath } from '../../../../model/entities/common';
import { CourseClassExtended } from '../../../../model/entities/CourseClass';
import { getEntityItemById } from '../../common/entityItemsService';
import { getAccessesByPath } from '../../common/utils';
import { GET_COURSE_CLASS, getCourseClassEnrolments, getCourseClassSessionsWarnings } from '../actions';
import CourseClassAssessmentService from '../components/assessments/services/CourseClassAssessmentService';
import { getCourseClassAttendance } from '../components/attendance/actions';
import { getCourseClassCosts } from '../components/budget/actions';
import CourseClassTimetableService from '../components/timetable/services/CourseClassTimetableService';
import { getCourseClassTutorsWarnings } from '../components/tutors/actions';
import CourseClassTutorService from '../components/tutors/services/CourseClassTutorService';

const request: EpicUtils.Request<{  courseClass: CourseClassExtended, access: AccessByPath[] }, number> = {
  type: GET_COURSE_CLASS,
  hideLoadIndicator: true,
  getData: async (id, state) => {

    const access = await getAccessesByPath(
      [
        courseClassBudgetPath,
        plainEnrolmentPath,
        courseClassTimetablePath
      ],
      state
    );

    const [
      courseClass,
      tutors,
      assessments,
      sessions
    ] = await Promise.all([
      getEntityItemById("CourseClass", id),
      CourseClassTutorService.getCourseClassTutors(id),
      CourseClassAssessmentService.getCourseClassAssessments(id),
      CourseClassTimetableService.findTimetableSessionsForCourseClasses(id)
    ]);

    sessions.sort((a, b) => (new Date(a.start) > new Date(b.start) ? 1 : -1));
    assessments.forEach((assessment: any, index) => { assessment.index = index; });

    courseClass.tutors = tutors;
    courseClass.assessments = assessments;
    courseClass.sessions = sessions;
    courseClass.studentAttendance = [];
    courseClass.trainingPlan = [];
    courseClass.budget = [];
    courseClass.notes = [];

    return { courseClass, access };
  },
  processData: ({ courseClass, access }, s, id) => {

    const relatedActions = [
      ...courseClass.tutors.length ? [getCourseClassTutorsWarnings(courseClass.tutors.map(t => t.id).toString())] : [],
      getCourseClassAttendance(id),
      getNoteItems("CourseClass", id, LIST_EDIT_VIEW_FORM_NAME),
      ...access.flatMap((accItem, index) => [
        ... index === 0 && accItem.hasAccess ? [getCourseClassCosts(id)] : [],
        ... index === 1 && accItem.hasAccess ? [getCourseClassEnrolments(id)] : [],
        ... index === 2 && accItem.hasAccess ? [getCourseClassSessionsWarnings(id, [...courseClass.sessions])] : [],
        ... accItem.action ? [accItem.action] : []
      ])
    ];

    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: {
          editRecord: courseClass,
          name: `${courseClass.courseName} ${courseClass.courseCode}-${courseClass.code}`
        }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, courseClass),
      ...relatedActions,
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetCourseClass: Epic<any, any> = EpicUtils.Create(request);