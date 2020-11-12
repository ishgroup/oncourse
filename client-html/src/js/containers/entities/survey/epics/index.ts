import { combineEpics } from "redux-observable";
import { EpicGetSurveyItem } from "./EpicGetSurveyItem";
import { EpicUpdateSurveyItem } from "./EpicUpdateSurveyItem";

export const EpicStudentFeedback = combineEpics(EpicGetSurveyItem, EpicUpdateSurveyItem);
