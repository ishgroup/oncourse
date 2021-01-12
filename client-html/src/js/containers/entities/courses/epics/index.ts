import { combineEpics } from "redux-observable";
import { EpicGetCourse } from "./EpicGetCourse";
import { EpicCreateCourse } from "./EpicCreateCourse";
import { EpicDeleteCourse } from "./EpicDeleteCourse";
import { EpicUpdateCourse } from "./EpicUpdateCourse";
import { EpicDuplicateCourse } from "./EpicDuplicateCourse";

export const EpicCourses = combineEpics(
  EpicGetCourse,
  EpicCreateCourse,
  EpicDeleteCourse,
  EpicUpdateCourse,
  EpicDuplicateCourse
);
