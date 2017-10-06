import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_HISTORY_REQUEST = _toRequestType("history/get/versions");
export const GET_HISTORY_FULFILLED = FULFILLED(GET_HISTORY_REQUEST);


export const getHistory = () => ({
  type: GET_HISTORY_REQUEST,
});
