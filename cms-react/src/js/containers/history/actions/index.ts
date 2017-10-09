import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_VERSIONS_REQUEST = _toRequestType("history/get/versions");
export const GET_VERSIONS_FULFILLED = FULFILLED(GET_VERSIONS_REQUEST);


export const getHistory = () => ({
  type: GET_VERSIONS_REQUEST,
});
