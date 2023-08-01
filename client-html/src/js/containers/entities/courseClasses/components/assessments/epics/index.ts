/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCreateCourseClassAssessment } from "./EpicCreateCourseClassAssessment";
import { EpicDeleteCourseClassAssessment } from "./EpicDeleteCourseClassAssessment";
import { EpicGetCourseClassAssessments } from "./EpicGetCourseClassAssessments";
import { EpicUpdateCourseClassAssessment } from "./EpicUpdateCourseClassAssessment";

export const EpicCourseClassAssestments = combineEpics(
  EpicGetCourseClassAssessments,
  EpicCreateCourseClassAssessment,
  EpicDeleteCourseClassAssessment,
  EpicUpdateCourseClassAssessment
);
