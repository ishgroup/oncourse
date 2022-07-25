import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { PriorLearning } from "@api/model";

export const UPDATE_PRIOR_LEARNING_ITEM = _toRequestType("put/priorLearning");
export const UPDATE_PRIOR_LEARNING_ITEM_FULFILLED = FULFILLED(UPDATE_PRIOR_LEARNING_ITEM);

export const updatePriorLearning = (id: string, priorLearning: PriorLearning) => ({
  type: UPDATE_PRIOR_LEARNING_ITEM,
  payload: { id, priorLearning }
});