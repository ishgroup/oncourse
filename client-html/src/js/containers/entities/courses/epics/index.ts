import { combineEpics } from "redux-observable";
import { EpicGetCourse } from "./EpicGetCourse";
import { EpicUpdateCourse } from "./EpicUpdateCourse";
import { EpicDuplicateCourse } from "./EpicDuplicateCourse";

export const EpicCourses = combineEpics(
  EpicGetCourse,
  EpicUpdateCourse,
  EpicDuplicateCourse
);