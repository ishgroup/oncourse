import { combineEpics } from "redux-observable";
import { EpicGetAssessment } from "./EpicGetAssessment";
import { EpicUpdateAssessmentItem } from "./EpicUpdateAssessmentItem";
import { EpicDeleteAssessment } from "./EpicDeleteAssessment";

export const EpicAssessment = combineEpics(
  EpicGetAssessment,
  EpicUpdateAssessmentItem,
  EpicDeleteAssessment,
);
