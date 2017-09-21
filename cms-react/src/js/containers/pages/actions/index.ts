import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_PAGES_REQUEST = _toRequestType("page/get/pages");
export const GET_PAGES_FULFILLED = FULFILLED(GET_PAGES_REQUEST);

export const SAVE_PAGE_SETTINGS_REQUEST = _toRequestType("page/save/settings");
export const SAVE_PAGE_SETTINGS_FULFILLED = FULFILLED(SAVE_PAGE_SETTINGS_REQUEST);

export const SAVE_PAGE_HTML_REQUEST = _toRequestType("page/save/html");
export const SAVE_PAGE_HTML_FULFILLED = FULFILLED(SAVE_PAGE_HTML_REQUEST);

export const getPages = () => ({
  type: GET_PAGES_REQUEST,
});

export const savePageSettings = (id, prop) => ({
  type: SAVE_PAGE_SETTINGS_REQUEST,
  payload: {id, ...prop},
});

export const savePageHtml = (id, html) => ({
  type: SAVE_PAGE_HTML_REQUEST,
  payload: {id, html},
});
