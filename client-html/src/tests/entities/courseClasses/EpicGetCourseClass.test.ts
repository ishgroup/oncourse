import { PermissionRequest } from '@api/model';
import { initialize } from 'redux-form';
import { checkPermissionsRequestFulfilled, clearActionsQueue } from '../../../js/common/actions';
import { getNoteItems } from '../../../js/common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../js/common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../js/common/components/list-view/constants';
import { courseClassBudgetPath, courseClassTimetablePath, plainEnrolmentPath } from '../../../js/constants/Api';
import {
  getCourseClass,
  getCourseClassEnrolments,
  getCourseClassSessionsWarnings
} from '../../../js/containers/entities/courseClasses/actions';
import { getCourseClassCosts } from '../../../js/containers/entities/courseClasses/components/budget/actions';
import { getCourseClassTutorsWarnings } from '../../../js/containers/entities/courseClasses/components/tutors/actions';
import { AccessByPath } from '../../../js/model/entities/common';
import {
  CourseClassExtended,
  StudentAttendanceExtended,
  TrainingPlanExtended
} from '../../../js/model/entities/CourseClass';
import { DefaultEpic } from '../../common/Default.Epic';

const id = 1;

const budgetAccessRequest: PermissionRequest = { path: courseClassBudgetPath, method: "GET" };
const enrolmentAccessRequest: PermissionRequest = { path: plainEnrolmentPath, method: "GET" };
const timetabletAccessRequest: PermissionRequest = { path: courseClassTimetablePath, method: "GET" };

describe("Get course class epic tests", () => {
  it("EpicGetCourseClass should returns correct values", async () => {
    const {EpicGetCourseClass} = await import('../../../js/containers/entities/courseClasses/epics/EpicGetCourseClass');
    return DefaultEpic({
      action: () => getCourseClass(id),
      epic: EpicGetCourseClass,
      processData: (mockedApi, s) => {
        const courseClassEx: CourseClassExtended = mockedApi.db.getCourseClass(id);
        courseClassEx.tutors = mockedApi.db.getCourseClassTutors(id.toString());
        courseClassEx.sessions = mockedApi.db.getCourseClassTimetable();
        courseClassEx.assessments = mockedApi.db.getCourseClassAssessment(id.toString());
        courseClassEx.studentAttendance = mockedApi.db.getCourseClassAttendanceStudents();
        courseClassEx.trainingPlan = mockedApi.db.getCourseClassTrainingPlan();
        courseClassEx.sessions.sort((a, b) => (new Date(a.start) > new Date(b.start) ? 1 : -1));
        courseClassEx.assessments.forEach((assessment: any, index) => { assessment.index = index; });
        courseClassEx.trainingPlan.forEach((t: TrainingPlanExtended, index) => {
          t.index = index;
        });

        courseClassEx.studentAttendance.forEach((s: StudentAttendanceExtended, index) => {
          s.index = index;
        });

        courseClassEx.budget = [];
        courseClassEx.notes = [];

        const budgetAccess: AccessByPath  = {
          hasAccess: true,
          action: checkPermissionsRequestFulfilled({
            ...budgetAccessRequest,
            hasAccess: true
          }),
        }
        const enrolmentAccess: AccessByPath = {
          hasAccess: true,
          action: checkPermissionsRequestFulfilled({
            ...enrolmentAccessRequest,
            hasAccess: true
          }),
        }

        const timetabletAccess: AccessByPath = {
          hasAccess: true,
          action: checkPermissionsRequestFulfilled({
            ...timetabletAccessRequest,
            hasAccess: true
          }),
        }

        const relatedActions = [
          ...courseClassEx.tutors.length ? [getCourseClassTutorsWarnings(courseClassEx.tutors.map(t => t.id).toString())] : [],
          getNoteItems("CourseClass", id, LIST_EDIT_VIEW_FORM_NAME),
          ...[budgetAccess, enrolmentAccess,timetabletAccess].flatMap((accItem, index) => [
            ... index === 0 && accItem.hasAccess ? [getCourseClassCosts(id)] : [],
            ... index === 1 && accItem.hasAccess ? [getCourseClassEnrolments(id)] : [],
            ... index === 2 && accItem.hasAccess ? [getCourseClassSessionsWarnings(id, [...courseClassEx.sessions])] : [],
            ... accItem.action ? [accItem.action] : []
          ])
        ];

        return [
          {
            type: SET_LIST_EDIT_RECORD,
            payload: {
              editRecord: courseClassEx,
              name: `${courseClassEx.courseName} ${courseClassEx.courseCode}-${courseClassEx.code}`
            }
          },
          initialize(LIST_EDIT_VIEW_FORM_NAME, courseClassEx),
          ...relatedActions,
          ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
        ];
      }
    })
  });
});