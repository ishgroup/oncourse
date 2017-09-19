import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_PAGES_REQUEST = _toRequestType("page/get/pages");
export const GET_PAGES_FULFILLED = FULFILLED(GET_PAGES_REQUEST);

export const EDIT_PAGE_SETTINGS = "page/update/settings";
export const EDIT_PAGE_CONTENT = "page/update/content";

export const getPages = () => ({
  type: GET_PAGES_REQUEST,
});

export const editPageSettings = (id, prop) => ({
  type: EDIT_PAGE_SETTINGS,
  payload: {id, ...prop},
});

export const editPageContent = content => ({
  type: EDIT_PAGE_CONTENT,
  payload: content,
});
