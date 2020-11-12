/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetCourseClassTutors } from "./EpicGetCourseClassTutors";
import { EpicPutCourseClassTutor } from "./EpicPutCourseClassTutor";
import { EpicPostCourseClassTutor } from "./EpicPostCourseClassTutor";
import { EpicDeleteCourseClassTutor } from "./EpicDeleteCourseClassTutor";

export const EpicCourseClassTutors = combineEpics(
  EpicGetCourseClassTutors,
  EpicPutCourseClassTutor,
  EpicPostCourseClassTutor,
  EpicDeleteCourseClassTutor
);
