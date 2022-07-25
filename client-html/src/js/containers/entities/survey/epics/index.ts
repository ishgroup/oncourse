import { combineEpics } from "redux-observable";
import { EpicUpdateSurveyItem } from "./EpicUpdateSurveyItem";

export const EpicStudentFeedback = combineEpics(EpicUpdateSurveyItem);