import { combineEpics } from "redux-observable";
import { EpicGetAssessment } from "./EpicGetAssessment";
import { EpicUpdateAssessmentItem } from "./EpicUpdateAssessmentItem";

export const EpicAssessment = combineEpics(
  EpicGetAssessment,
  EpicUpdateAssessmentItem
);