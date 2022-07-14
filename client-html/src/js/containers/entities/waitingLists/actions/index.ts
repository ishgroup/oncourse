import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { WaitingList } from "@api/model";

export const GET_WAITING_LIST_ITEM = _toRequestType("get/waitingList");
export const GET_WAITING_LIST_ITEM_FULFILLED = FULFILLED(GET_WAITING_LIST_ITEM);

export const DELETE_WAITING_LIST_ITEM = _toRequestType("delete/waitingList");
export const DELETE_WAITING_LIST_ITEM_FULFILLED = FULFILLED(DELETE_WAITING_LIST_ITEM);

export const UPDATE_WAITING_LIST_ITEM = _toRequestType("put/waitingList");
export const UPDATE_WAITING_LIST_ITEM_FULFILLED = FULFILLED(UPDATE_WAITING_LIST_ITEM);

export const getWaitingList = (id: string) => ({
  type: GET_WAITING_LIST_ITEM,
  payload: id
});

export const removeWaitingList = (id: string) => ({
  type: DELETE_WAITING_LIST_ITEM,
  payload: id
});

export const updateWaitingList = (id: string, waitingList: WaitingList) => ({
  type: UPDATE_WAITING_LIST_ITEM,
  payload: { id, waitingList }
});
