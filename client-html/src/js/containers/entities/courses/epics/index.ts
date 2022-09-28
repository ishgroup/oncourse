import { combineEpics } from "redux-observable";
import { EpicDuplicateCourse } from "./EpicDuplicateCourse";

export const EpicCourses = combineEpics(
  EpicDuplicateCourse
);