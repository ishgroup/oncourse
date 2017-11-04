import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_PAGES_REQUEST = _toRequestType("page/get/pages");
export const GET_PAGES_FULFILLED = FULFILLED(GET_PAGES_REQUEST);

export const SAVE_PAGE_REQUEST = _toRequestType("page/save");
export const SAVE_PAGE_FULFILLED = FULFILLED(SAVE_PAGE_REQUEST);

export const ADD_PAGE_REQUEST = _toRequestType("page/add");
export const ADD_PAGE_FULFILLED = FULFILLED(ADD_PAGE_REQUEST);

export const DELETE_PAGE_REQUEST = _toRequestType("page/delete");
export const DELETE_PAGE_FULFILLED = FULFILLED(DELETE_PAGE_REQUEST);

export const GET_PAGE_BY_URL_REQUEST = _toRequestType("page/get/page/byUrl");
export const GET_PAGE_BY_URL_FULFILLED = FULFILLED(GET_PAGE_BY_URL_REQUEST);

export const GET_PAGE_RENDER_REQUEST = _toRequestType("page/get/page/render");
export const GET_PAGE_RENDER_FULFILLED = FULFILLED(GET_PAGE_RENDER_REQUEST);

export const TOGGLE_EDIT_MODE = "page/toggle/edit/mode";
export const CLEAR_RENDER_HTML = "page/clear/render/html";

export const getPages = () => ({
  type: GET_PAGES_REQUEST,
});

export const savePage = (id, props, updateRender: boolean = false) => ({
  type: SAVE_PAGE_REQUEST,
  payload: {id, ...props, updateRender},
});

export const addPage = () => ({
  type: ADD_PAGE_REQUEST,
});

export const deletePage = id => ({
  type: DELETE_PAGE_REQUEST,
  payload: id,
});

export const getPageByUrl = url => ({
  type: GET_PAGE_BY_URL_REQUEST,
  payload: url,
});

export const toggleEditMode = flag => ({
  type: TOGGLE_EDIT_MODE,
  payload: flag,
});

export const getPageRender = id => ({
  type: GET_PAGE_RENDER_REQUEST,
  payload: {id},
});

export const clearRenderHtml = id => ({
  type: CLEAR_RENDER_HTML,
  payload: id,
});
