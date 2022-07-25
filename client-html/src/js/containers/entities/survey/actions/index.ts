import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { SurveyItem } from "@api/model";

export const UPDATE_SURVEY_ITEM = _toRequestType("put/survey");
export const UPDATE_SURVEY_ITEM_FULFILLED = FULFILLED(UPDATE_SURVEY_ITEM);

export const updateSurveyItem = (id: string, survey: SurveyItem) => ({
  type: UPDATE_SURVEY_ITEM,
  payload: { id, survey }
});