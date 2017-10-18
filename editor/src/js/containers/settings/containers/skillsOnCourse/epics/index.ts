import {combineEpics} from "redux-observable";
import {EpicGetSkillsOnCourseSettings} from "./EpicGetSkillsOnCourseSettings";
import {EpicSetSkillsOnCourseSettings} from "./EpicSetSkillsOnCourseSettings";

export const EpicSkillsOnCourseSettings = combineEpics(
  EpicGetSkillsOnCourseSettings,
  EpicSetSkillsOnCourseSettings,
);
