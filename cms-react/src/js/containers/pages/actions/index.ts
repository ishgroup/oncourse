import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_PAGES_REQUEST = _toRequestType("page/get/pages");
export const GET_PAGES_FULFILLED = FULFILLED(GET_PAGES_REQUEST);

export const getPages = () => ({
  type: GET_PAGES_REQUEST,
});
