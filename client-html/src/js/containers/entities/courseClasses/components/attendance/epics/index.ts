/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetCourseClassAttendance } from "./EpicGetCourseClassAttendance";
import { EpicUpdateCourseClassStudentAttendance } from "./EpicUpdateCourseClassStudentAttendance";
import { EpicUpdateCourseClassTrainingPlans } from "./EpicUpdateCourseClassTrainingPlans";
import { EpicUpdateCourseClassTutorAttendance } from "./EpicUpdateCourseClassTutorAttendance";

export const EpicCourseClassAttendance = combineEpics(
  EpicGetCourseClassAttendance,
  EpicUpdateCourseClassStudentAttendance,
  EpicUpdateCourseClassTrainingPlans,
  EpicUpdateCourseClassTutorAttendance
);
