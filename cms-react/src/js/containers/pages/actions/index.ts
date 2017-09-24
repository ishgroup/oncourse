import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_PAGES_REQUEST = _toRequestType("page/get/pages");
export const GET_PAGES_FULFILLED = FULFILLED(GET_PAGES_REQUEST);

export const SAVE_PAGE_REQUEST = _toRequestType("page/save");
export const SAVE_PAGE_FULFILLED = FULFILLED(SAVE_PAGE_REQUEST);

export const getPages = () => ({
  type: GET_PAGES_REQUEST,
});

export const savePage = (id, props) => ({
  type: SAVE_PAGE_REQUEST,
  payload: {id, ...props},
});
