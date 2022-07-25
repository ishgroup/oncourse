import { Diff, Document } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const UPDATE_DOCUMENT_ITEM = _toRequestType("put/document");
export const UPDATE_DOCUMENT_ITEM_FULFILLED = FULFILLED(UPDATE_DOCUMENT_ITEM);

export const DELETE_DOCUMENT_ITEM = _toRequestType("delete/document");
export const RESTORE_DOCUMENT = _toRequestType("patch/document");

export const restoreDocument = (diff: Diff) => ({
  type: RESTORE_DOCUMENT,
  payload: diff
});

export const removeDocument = (diff: Diff) => ({
  type: DELETE_DOCUMENT_ITEM,
  payload: diff
});

export const updateDocument = (id: string, document: Document) => ({
  type: UPDATE_DOCUMENT_ITEM,
  payload: { id, document }
});