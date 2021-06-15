import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";
import {ContentMode} from "../../../../../model";

export const GET_BLOCKS_REQUEST = _toRequestType("block/get/blocks");
export const GET_BLOCKS_FULFILLED = FULFILLED(GET_BLOCKS_REQUEST);

export const GET_BLOCK_REQUEST = "GET_BLOCK_REQUEST";
export const GET_BLOCK_FULFILLED = "GET_BLOCK_FULFILLED";

export const SAVE_BLOCK_REQUEST = _toRequestType("block/save");
export const SAVE_BLOCK_FULFILLED = FULFILLED(SAVE_BLOCK_REQUEST);
export const GET_BLOCK_RENDER_FULFILLED = "GET_BLOCK_RENDER_FULFILLED";
export const CLEAR_BLOCK_RENDER_HTML = "CLEAR_BLOCK_RENDER_HTML";

export const ADD_BLOCK_REQUEST = _toRequestType("block/add");
export const ADD_BLOCK_FULFILLED = FULFILLED(ADD_BLOCK_REQUEST);

export const DELETE_BLOCK_REQUEST = _toRequestType("block/delete");
export const DELETE_BLOCK_FULFILLED = FULFILLED(DELETE_BLOCK_REQUEST);

export const SET_BLOCK_CONTENT_MODE = "block/set/contentMode";

export const setBlockContentMode = (id: number, contentMode: ContentMode) => ({
  type: SET_BLOCK_CONTENT_MODE,
  payload: {id, contentMode},
});

export const getBlock = (id: string) => ({
  type: GET_BLOCK_REQUEST,
  payload: id,
})

export const getBlocks = () => ({
  type: GET_BLOCKS_REQUEST,
});

export const saveBlock = (id, props, updatePageRender = false) => ({
  type: SAVE_BLOCK_REQUEST,
  payload: {id, ...props, updatePageRender},
});

export const clearBlockRenderHtml = () => ({
  type: CLEAR_BLOCK_RENDER_HTML,
});

export const deleteBlock = id => ({
  type: DELETE_BLOCK_REQUEST,
  payload: id,
});

export const addBlock = () => ({
  type: ADD_BLOCK_REQUEST,
});
