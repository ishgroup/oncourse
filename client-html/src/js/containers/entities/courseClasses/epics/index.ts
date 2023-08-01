/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCourseClassAssestments } from "../components/assessments/epics";
import { EpicCourseClassAttendance } from "../components/attendance/epics";
import { EpicCourseClassBudget } from "../components/budget/epics";
import { EpicCourseClassTimeTable } from "../components/timetable/epics";
import { EpicCourseClassTutors } from "../components/tutors/epics";
import { EpicCancelCourseClass } from "./EpicCancelCourseClass";
import { EpicCreateCourseClass } from "./EpicCreateCourseClass";
import { EpicDuplicateCourseClass } from "./EpicDuplicateCourseClass";
import { EpicGetCourseClass } from "./EpicGetCourseClass";
import { EpicGetCourseClassEnrolments } from "./EpicGetCourseClassEnrolments";
import { EpicGetCourseClassSessionsWarnings } from "./EpicGetCourseClassSessionsWarnings";
import { EpicGetCourseClassTags } from "./EpicGetCourseClassTags";
import { EpicGetDuplicateCourseClassesSessions } from "./EpicGetDuplicateCourseClassesSessions";
import { EpicGetDuplicateTraineeshipBudget } from "./EpicGetDuplicateTraineeshipBudget";
import { EpicUpdateCourseClass } from "./EpicUpdateCourseClass";

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
  EpicGetCourseClassSessionsWarnings,
  EpicGetDuplicateTraineeshipBudget
);