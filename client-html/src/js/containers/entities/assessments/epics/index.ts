import { combineEpics } from "redux-observable";
import { EpicUpdateAssessmentItem } from "./EpicUpdateAssessmentItem";

export const EpicAssessment = combineEpics(
  EpicUpdateAssessmentItem
);