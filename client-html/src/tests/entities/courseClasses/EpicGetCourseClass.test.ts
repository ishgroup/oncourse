import { PermissionRequest } from '@api/model';
import { initialize } from 'redux-form';
import { checkPermissionsRequestFulfilled } from '../../../js/common/actions';
import { getNoteItems } from '../../../js/common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../js/common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../js/common/components/list-view/constants';
import { courseClassBudgetPath, plainEnrolmentPath } from '../../../js/constants/Api';
import { GET_COURSE_CLASS_ENROLMENTS, getCourseClass } from '../../../js/containers/entities/courseClasses/actions';
import {
  GET_COURSE_CLASS_ATTENDANCE
} from '../../../js/containers/entities/courseClasses/components/attendance/actions';
import { GET_COURSE_CLASS_COSTS } from '../../../js/containers/entities/courseClasses/components/budget/actions';
import { GET_COURSE_CLASS_SESSIONS } from '../../../js/containers/entities/courseClasses/components/timetable/actions';
import { AccessByPath } from '../../../js/model/entities/common';
import { CourseClassExtended } from '../../../js/model/entities/CourseClass';
import { DefaultEpic } from '../../common/Default.Epic';

const id = 1;

const budgetAccessRequest: PermissionRequest = { path: courseClassBudgetPath, method: "GET" };
const enrolmentAccessRequest: PermissionRequest = { path: plainEnrolmentPath, method: "GET" };

//TODO: resolve

// describe("Get course class epic tests", () => {
//   it("EpicGetCourseClass should returns correct values", async () => {
//     const {EpicGetCourseClass} = await import('../../../js/containers/entities/courseClasses/epics/EpicGetCourseClass');
//     return DefaultEpic({
//       action: () => getCourseClass(id),
//       epic: EpicGetCourseClass,
//       processData: async mockedApi => {
//         const courseClassEx: CourseClassExtended = mockedApi.db.getCourseClass(id);
//         courseClassEx.tutors = mockedApi.db.getCourseClassTutors(id);
//         courseClassEx.sessions = mockedApi.db.getCourseClassTimetable();
//         courseClassEx.assessments = mockedApi.db.getCourseClassAssessment(id);
//         courseClassEx.studentAttendance = [];
//         courseClassEx.trainingPlan = [];
//         courseClassEx.budget = [];
//         courseClassEx.notes = [];
//
//
//         const budgetAccess: AccessByPath  = {
//           hasAccess: true,
//           action: checkPermissionsRequestFulfilled({
//             ...budgetAccessRequest,
//             hasAccess: true
//           }),
//         }
//         const enrolmentAccess: AccessByPath = {
//           hasAccess: true,
//           action: checkPermissionsRequestFulfilled({
//             ...enrolmentAccessRequest,
//             hasAccess: true
//           }),
//         }
//
//         const relatedActions = [
//           {
//             type: GET_COURSE_CLASS_SESSIONS,
//             payload: id
//           },
//           {
//             type: GET_COURSE_CLASS_ATTENDANCE,
//             payload: id
//           },
//           ...[
//             budgetAccess,
//             enrolmentAccess
//           ].filter(p => p.action).map(p => p.action)
//         ];
//
//         relatedActions.push({
//           type: GET_COURSE_CLASS_COSTS,
//           payload: id
//         });
//
//         relatedActions.push({
//           type: GET_COURSE_CLASS_ENROLMENTS,
//           payload: id
//         });
//
//         return [
//           {
//             type: SET_LIST_EDIT_RECORD,
//             payload: {
//               editRecord: courseClassEx,
//               name: `${courseClassEx.courseName} ${courseClassEx.courseCode}-${courseClassEx.code}`
//             }
//           },
//           ...relatedActions,
//           initialize(LIST_EDIT_VIEW_FORM_NAME, courseClassEx),
//           getNoteItems("CourseClass", id, LIST_EDIT_VIEW_FORM_NAME)
//         ];
//       }
//     })
//   });
// });
