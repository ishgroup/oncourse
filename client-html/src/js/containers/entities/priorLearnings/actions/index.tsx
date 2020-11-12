import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { PriorLearning } from "@api/model";

export const GET_PRIOR_LEARNING_ITEM = _toRequestType("get/priorLearning");
export const GET_PRIOR_LEARNING_ITEM_FULFILLED = FULFILLED(GET_PRIOR_LEARNING_ITEM);

export const UPDATE_PRIOR_LEARNING_ITEM = _toRequestType("put/priorLearning");
export const UPDATE_PRIOR_LEARNING_ITEM_FULFILLED = FULFILLED(UPDATE_PRIOR_LEARNING_ITEM);

export const CREATE_PRIOR_LEARNING_ITEM = _toRequestType("create/priorLearning");
export const CREATE_PRIOR_LEARNING_ITEM_FULFILLED = FULFILLED(CREATE_PRIOR_LEARNING_ITEM);

export const DELETE_PRIOR_LEARNING_ITEM = _toRequestType("delete/priorLearning");
export const DELETE_PRIOR_LEARNING_ITEM_FULFILLED = FULFILLED(DELETE_PRIOR_LEARNING_ITEM);

export const getPriorLearning = (id: string) => ({
  type: GET_PRIOR_LEARNING_ITEM,
  payload: id
});

export const updatePriorLearning = (id: string, priorLearning: PriorLearning) => ({
  type: UPDATE_PRIOR_LEARNING_ITEM,
  payload: { id, priorLearning }
});

export const createPriorLearning = (priorLearning: PriorLearning) => ({
  type: CREATE_PRIOR_LEARNING_ITEM,
  payload: priorLearning
});

export const deletePriorLearning = (id: string) => ({
  type: DELETE_PRIOR_LEARNING_ITEM,
  payload: id
});
