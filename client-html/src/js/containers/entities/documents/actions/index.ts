import { Diff, Document } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_DOCUMENT_EDIT = _toRequestType("get/list/entity/document/edit");
export const GET_DOCUMENT_EDIT_FULFILLED = FULFILLED(GET_DOCUMENT_EDIT);

export const DELETE_DOCUMENT_ITEM = _toRequestType("delete/document");
export const DELETE_DOCUMENT_ITEM_FULFILLED = FULFILLED(DELETE_DOCUMENT_ITEM);

export const UPDATE_DOCUMENT_ITEM = _toRequestType("put/document");
export const UPDATE_DOCUMENT_ITEM_FULFILLED = FULFILLED(UPDATE_DOCUMENT_ITEM);

export const RESTORE_DOCUMENT = _toRequestType("patch/document");

export const CREATE_DOCUMENT_ITEM = _toRequestType("post/document");
export const CREATE_DOCUMENT_ITEM_FULFILLED = FULFILLED(CREATE_DOCUMENT_ITEM);

export const GET_DOCUMENT_ITEMS = _toRequestType("get/document");

export const restoreDocument = (diff: Diff) => ({
  type: RESTORE_DOCUMENT,
  payload: diff
});

export const getDocument = (id: number) => ({
  type: GET_DOCUMENT_EDIT,
  payload: id
});

export const removeDocument = (id: string) => ({
  type: DELETE_DOCUMENT_ITEM,
  payload: id
});

export const updateDocument = (id: string, document: Document) => ({
  type: UPDATE_DOCUMENT_ITEM,
  payload: { id, document }
});

export const createDocument = (document: Document) => {
  const { id } = document;
  if (id) {
    return ({
      type: UPDATE_DOCUMENT_ITEM,
      payload: { id, document }
    });
  }
  return ({
    type: CREATE_DOCUMENT_ITEM,
    payload: { document }
  });
};
