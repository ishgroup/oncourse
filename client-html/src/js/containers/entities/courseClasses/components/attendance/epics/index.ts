/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicUpdateCourseClassStudentAttendance } from "./EpicUpdateCourseClassStudentAttendance";
import { EpicUpdateCourseClassTrainingPlans } from "./EpicUpdateCourseClassTrainingPlans";

export const EpicCourseClassAttendance = combineEpics(
  EpicUpdateCourseClassStudentAttendance,
  EpicUpdateCourseClassTrainingPlans
);
