/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Document } from "@api/model";
import { DocumentExtended } from "../../../../../model/common/Documents";
import { _toRequestType } from "../../../../actions/ActionUtils";

export const CREATE_DOCUMENT = _toRequestType("post/list/entity/document");

export const CREATE_AVATAR_DOCUMENT = _toRequestType("post/list/entity/document/avatar");

export const GET_DOCUMENT = _toRequestType("get/list/entity/document");

export const SEARCH_DOCUMENT_BY_HASH = _toRequestType("get/list/entity/document/search/hash");

export const SEARCH_DOCUMENT_BY_NAME = _toRequestType("get/list/entity/document/search/name");

export const CLEAR_EDITING_DOCUMENT = "clear/editingDocument";

export const SET_EDITING_DOCUMENT = "set/editingDocument";

export const SET_DOCUMENT_FILE = "set/documentFile";

export const SET_SEARCH_DOCUMENTS = "set/searchDocuments";

export const createDocument = (document: Document, form: string, documentPath: string, index: number) => ({
  type: CREATE_DOCUMENT,
  payload: {
    document, form, documentPath, index
  }
});

export const createAvatarDocument = (document: DocumentExtended, form: string, documentPath: string) => ({
  type: CREATE_AVATAR_DOCUMENT,
  payload: {document, form, documentPath}
});

export const getDocumentItem = (id: number, editingFormName: string) => ({
  type: GET_DOCUMENT,
  payload: {id, editingFormName}
});

export const searchDocumentByHash = (inputDocument: File, editingFormName: string) => ({
  type: SEARCH_DOCUMENT_BY_HASH,
  payload: {inputDocument, editingFormName}
});

export const searchDocumentByName = (documentName: string, editingFormName: string) => ({
  type: SEARCH_DOCUMENT_BY_NAME,
  payload: {documentName, editingFormName}
});

export const clearEditingDocument = () => ({
  type: CLEAR_EDITING_DOCUMENT,
  payload: {
    editingDocument: null, editingFormName: null, documentFile: null, viewDocument: false
  }
});

export const setEditingDocument = (editingDocument: Document, editingFormName: string, viewDocument: boolean = false) => ({
  type: SET_EDITING_DOCUMENT,
  payload: {editingDocument, editingFormName, viewDocument}
});

export const setDocumentFile = (documentFile: File) => ({
  type: SET_DOCUMENT_FILE,
  payload: {documentFile}
});

export const setSearchDocuments = (searchDocuments: any) => ({
  type: SET_SEARCH_DOCUMENTS,
  payload: {searchDocuments}
});
