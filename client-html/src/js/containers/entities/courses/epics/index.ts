import { combineEpics } from "redux-observable";
import { EpicGetPlainCourses } from "./EpicGetPlainCourses";
import { EpicGetCourse } from "./EpicGetCourse";
import { EpicCreateCourse } from "./EpicCreateCourse";
import { EpicDeleteCourse } from "./EpicDeleteCourse";
import { EpicUpdateCourse } from "./EpicUpdateCourse";
import { EpicDuplicateCourse } from "./EpicDuplicateCourse";

export const EpicCourses = combineEpics(
  EpicGetPlainCourses,
  EpicGetCourse,
  EpicCreateCourse,
  EpicDeleteCourse,
  EpicUpdateCourse,
  EpicDuplicateCourse
);
