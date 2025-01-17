/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicDeleteCourseClassTutor } from "./EpicDeleteCourseClassTutor";
import { EpicGetCourseClassTutors } from "./EpicGetCourseClassTutors";
import { EpicPostCourseClassTutor } from "./EpicPostCourseClassTutor";
import { EpicPutCourseClassTutor } from "./EpicPutCourseClassTutor";

export const EpicCourseClassTutors = combineEpics(
  EpicGetCourseClassTutors,
  EpicPutCourseClassTutor,
  EpicPostCourseClassTutor,
  EpicDeleteCourseClassTutor
);
