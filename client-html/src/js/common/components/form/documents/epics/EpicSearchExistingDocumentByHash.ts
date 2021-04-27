/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { from } from "rxjs";
import { Document } from "@api/model";
import { SHOW_MESSAGE } from "../../../../actions";
import * as EpicUtils from "../../../../epics/EpicUtils";
import DocumentsService from "../services/DocumentsService";
import { SEARCH_DOCUMENT_BY_HASH, SET_DOCUMENT_FILE, SET_EDITING_DOCUMENT } from "../actions";
import { getInitialDocument } from "../components/utils";
import { IAction } from "../../../../actions/IshAction";

const getEditingDocumentAction = (
  file: File,
  editingFormName: string
): Promise<IAction<{ editingDocument: Document; editingFormName: string }>> =>
  getInitialDocument(file).then(editingDocument => ({
    type: SET_EDITING_DOCUMENT,
    payload: { editingDocument, editingFormName }
  }));

const request: EpicUtils.Request<any, { inputDocument: File; editingFormName: string }> = {
  type: SEARCH_DOCUMENT_BY_HASH,
  hideLoadIndicator: true,
  getData: ({ inputDocument }) => DocumentsService.searchDocument(inputDocument),
  processData: (editingDocument: Document, state: any, { inputDocument, editingFormName }) => {
    if (editingDocument && !editingDocument.shared) {
      return [{
        type: SHOW_MESSAGE,
        payload: {
          message: "Document is already uploaded and has restricted status"
        }
      }];
    }
    if (editingDocument && editingDocument.removed) {
      return [{
          type: SHOW_MESSAGE,
          payload: {
              message: "This document was moved to the bin. If you need it, you could restore it and then try again."
          }
      }];
    }
    return editingDocument
      ? [
          {
            type: SET_EDITING_DOCUMENT,
            payload: { editingDocument, editingFormName }
          },
          {
            type: SET_DOCUMENT_FILE,
            payload: { documentFile: null }
          }
        ]
      : from(getEditingDocumentAction(inputDocument, editingFormName));
  }
};

export const EpicSearchExistingDocumentByHash: Epic<any, any> = EpicUtils.Create(request);
