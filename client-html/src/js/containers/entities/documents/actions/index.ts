import { Diff } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";

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