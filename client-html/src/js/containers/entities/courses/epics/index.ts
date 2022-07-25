import { combineEpics } from "redux-observable";
import { EpicUpdateCourse } from "./EpicUpdateCourse";
import { EpicDuplicateCourse } from "./EpicDuplicateCourse";

export const EpicCourses = combineEpics(
  EpicUpdateCourse,
  EpicDuplicateCourse
);