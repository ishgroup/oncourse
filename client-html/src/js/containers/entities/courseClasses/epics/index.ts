/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCourseClassTutors } from "../components/tutors/epics";
import { EpicCourseClassBudget } from "../components/budget/epics";
import { EpicCourseClassTimeTable } from "../components/timetable/epics";
import { EpicCourseClassAssestments } from "../components/assessments/epics";
import { EpicCourseClassAttendance } from "../components/attendance/epics";
import { EpicDuplicateCourseClass } from "./EpicDuplicateCourseClass";
import { EpicGetDuplicateCourseClassesSessions } from "./EpicGetDuplicateCourseClassesSessions";
import { EpicGetCourseClass } from "./EpicGetCourseClass";
import { EpicGetCourseClassEnrolments } from "./EpicGetCourseClassEnrolments";
import { EpicCancelCourseClass } from "./EpicCancelCourseClass";
import { EpicGetCourseClassTags } from "./EpicGetCourseClassTags";
import { EpicUpdateCourseClass } from "./EpicUpdateCourseClass";
import { EpicCreateCourseClass } from "./EpicCreateCourseClass";
import { EpicDeleteCourseClass } from "./EpicDeleteCourseClass";
import { EpicGetCourseClassSessionsWarnings } from "./EpicGetCourseClassSessionsWarnings";
import { EpicGetDuplicateTraineeshipBudget } from "./EpicGetDuplicateTraineeshipBudget";

export const EpicCourseClass = combineEpics(
  EpicCourseClassTutors,
  EpicCourseClassBudget,
  EpicCourseClassTimeTable,
  EpicCourseClassAssestments,
  EpicCourseClassAttendance,
  EpicDuplicateCourseClass,
  EpicGetDuplicateCourseClassesSessions,
  EpicGetCourseClass,
  EpicGetCourseClassEnrolments,
  EpicCancelCourseClass,
  EpicGetCourseClassTags,
  EpicUpdateCourseClass,
  EpicCreateCourseClass,
  EpicDeleteCourseClass,
  EpicGetCourseClassSessionsWarnings,
  EpicGetDuplicateTraineeshipBudget
);
