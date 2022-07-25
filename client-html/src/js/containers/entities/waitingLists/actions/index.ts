import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { WaitingList } from "@api/model";

export const GET_WAITING_LIST_ITEM = _toRequestType("get/waitingList");
export const GET_WAITING_LIST_ITEM_FULFILLED = FULFILLED(GET_WAITING_LIST_ITEM);

export const UPDATE_WAITING_LIST_ITEM = _toRequestType("put/waitingList");
export const UPDATE_WAITING_LIST_ITEM_FULFILLED = FULFILLED(UPDATE_WAITING_LIST_ITEM);

export const updateWaitingList = (id: string, waitingList: WaitingList) => ({
  type: UPDATE_WAITING_LIST_ITEM,
  payload: { id, waitingList }
});
