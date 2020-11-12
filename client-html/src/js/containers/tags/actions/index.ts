import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import { Tag } from "@api/model";

export const GET_ALL_TAGS_REQUEST = _toRequestType("get/tag");
export const GET_ALL_TAGS_FULFILLED = FULFILLED(GET_ALL_TAGS_REQUEST);

export const GET_LIST_TAGS_REQUEST = _toRequestType("get/listView/tag");
export const GET_LIST_TAGS_FULFILLED = FULFILLED(GET_LIST_TAGS_REQUEST);

export const GET_ENTITY_TAGS_REQUEST = _toRequestType("get/entity/tag");
export const GET_ENTITY_TAGS_REQUEST_FULFILLED = FULFILLED(GET_ENTITY_TAGS_REQUEST);

export const UPDATE_TAG_REQUEST = _toRequestType("put/tag");
export const UPDATE_TAG_REQUEST_FULFILLED = FULFILLED(UPDATE_TAG_REQUEST);

export const CREATE_TAG_REQUEST = _toRequestType("post/tag");
export const CREATE_TAG_REQUEST_FULFILLED = FULFILLED(CREATE_TAG_REQUEST);

export const DELETE_TAG_REQUEST = _toRequestType("delete/tag");
export const DELETE_TAG_REQUEST_FULFILLED = FULFILLED(DELETE_TAG_REQUEST);

export const UPDATE_TAG_EDIT_VIEW_STATE = "update/tag/editView/state";

export const updateTagEditViewState = (item: Tag, open: boolean, parent: string) => ({
  type: UPDATE_TAG_EDIT_VIEW_STATE,
  payload: { item, open, parent }
});

export const deleteTag = (id: number) => ({
  type: DELETE_TAG_REQUEST,
  payload: { id }
});

export const createTag = (tag: Tag) => ({
  type: CREATE_TAG_REQUEST,
  payload: { tag }
});

export const updateTag = (id: number, tag: Tag) => ({
  type: UPDATE_TAG_REQUEST,
  payload: { id, tag }
});

export const getAllTags = () => ({
  type: GET_ALL_TAGS_REQUEST
});

export const getListTags = (entityName: string) => ({
  type: GET_LIST_TAGS_REQUEST,
  payload: { entityName }
});

export const getEntityTags = (entityName: string) => ({
  type: GET_ENTITY_TAGS_REQUEST,
  payload: { entityName }
});
