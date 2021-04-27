import { combineEpics } from "redux-observable";
import { EpicGetAssessment } from "./EpicGetAssessment";
import { EpicUpdateAssessmentItem } from "./EpicUpdateAssessmentItem";
import { EpicCreateAssessment } from "./EpicCreateAssessment";
import { EpicDeleteAssessment } from "./EpicDeleteAssessment";

export const EpicAssessment = combineEpics(
  EpicGetAssessment,
  EpicUpdateAssessmentItem,
  EpicCreateAssessment,
  EpicDeleteAssessment,
);
