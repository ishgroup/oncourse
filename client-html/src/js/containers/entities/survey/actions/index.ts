import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { SurveyItem } from "@api/model";

export const GET_STUDENT_SURVEY_ITEM = _toRequestType("get/survey");
export const GET_STUDENT_SURVEY_ITEM_FULFILLED = FULFILLED(GET_STUDENT_SURVEY_ITEM);

export const UPDATE_SURVEY_ITEM = _toRequestType("put/survey");
export const UPDATE_SURVEY_ITEM_FULFILLED = FULFILLED(UPDATE_SURVEY_ITEM);

export const getSurveyItem = (id: string) => ({
  type: GET_STUDENT_SURVEY_ITEM,
  payload: id
});

export const updateSurveyItem = (id: string, survey: SurveyItem) => ({
  type: UPDATE_SURVEY_ITEM,
  payload: { id, survey }
});
