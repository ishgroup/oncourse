import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_VERSIONS_REQUEST = _toRequestType("history/get/versions");
export const GET_VERSIONS_FULFILLED = FULFILLED(GET_VERSIONS_REQUEST);

export const PUBLISH_REQUEST = _toRequestType('history/publish');
export const PUBLISH_FULFILLED = FULFILLED(PUBLISH_REQUEST);

export const SET_VERSION_REQUEST = _toRequestType('history/set/version');
export const SET_VERSION_FULFILLED = FULFILLED(SET_VERSION_REQUEST);

export const getHistory = () => ({
  type: GET_VERSIONS_REQUEST,
});

export const publish = (id, status, description) => ({
  type: PUBLISH_REQUEST,
  payload: {id, status, description},
});

export const setVersion = (id, status) => ({
  type: SET_VERSION_REQUEST,
  payload: {id, status}
});
