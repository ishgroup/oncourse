import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";

export const GET_BLOCKS_REQUEST = _toRequestType("block/get/blocks");
export const GET_BLOCKS_FULFILLED = FULFILLED(GET_BLOCKS_REQUEST);

export const SAVE_BLOCK_REQUEST = _toRequestType("block/save");
export const SAVE_BLOCK_FULFILLED = FULFILLED(SAVE_BLOCK_REQUEST);

export const ADD_BLOCK_REQUEST = _toRequestType("block/add");
export const ADD_BLOCK_FULFILLED = FULFILLED(ADD_BLOCK_REQUEST);

export const DELETE_BLOCK_REQUEST = _toRequestType("block/delete");
export const DELETE_BLOCK_FULFILLED = FULFILLED(DELETE_BLOCK_REQUEST);

export const getBlocks = () => ({
  type: GET_BLOCKS_REQUEST,
});

export const saveBlock = (id, props) => ({
  type: SAVE_BLOCK_REQUEST,
  payload: {id, ...props},
});

export const deleteBlock = id => ({
  type: DELETE_BLOCK_REQUEST,
  payload: id,
});

export const addBlock = () => ({
  type: ADD_BLOCK_REQUEST,
});
