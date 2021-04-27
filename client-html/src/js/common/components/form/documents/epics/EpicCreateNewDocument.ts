/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Document } from "@api/model";
import { arrayPush, arrayRemove } from "redux-form";
import * as EpicUtils from "../../../../epics/EpicUtils";
import DocumentsService from "../services/DocumentsService";
import { CREATE_DOCUMENT, SET_DOCUMENT_FILE, SET_EDITING_DOCUMENT } from "../actions";
import FetchErrorHandler from "../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { State } from "../../../../../reducers/state";

const request: EpicUtils.Request<
  any,
  { document: Document; form: string; documentPath: string; index: number }
> = {
  type: CREATE_DOCUMENT,
  hideLoadIndicator: true,
  getData: ({ document }, state: State) =>
    DocumentsService.createDocument(
      document.name,
      document.description,
      document.shared,
      document.access,
      state.documents.documentFile,
      document.tags.map(t => t.id).toString(),
      state.documents.documentFile ? state.documents.documentFile.name : ""
    ),
  processData: (newDocument: Document, state: any, { form, documentPath, index }) => [
      {
        type: SET_EDITING_DOCUMENT,
        payload: { editingDocument: null, editingFormName: null }
      },
      {
        type: SET_DOCUMENT_FILE,
        payload: { documentFile: null }
      },
      arrayRemove(form, documentPath, index),
      arrayPush(form, documentPath, newDocument)
    ],
  processError: (error, { form, documentPath, index }) => [
    {
      type: SET_EDITING_DOCUMENT,
      payload: { editingDocument: null, editingFormName: null }
    },
    {
      type: SET_DOCUMENT_FILE,
      payload: { documentFile: null }
    },
    arrayRemove(form, documentPath, index),
    ...FetchErrorHandler(error)
  ]
};

export const EpicCreateNewDocument: Epic<any, any> = EpicUtils.Create(request);
