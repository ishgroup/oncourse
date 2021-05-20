/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Document } from "@api/model";
import * as EpicUtils from "../../../../epics/EpicUtils";
import { GET_DOCUMENT, SET_EDITING_DOCUMENT } from "../actions";
import DocumentsService from "../services/DocumentsService";

const request: EpicUtils.Request<any, { id: number; editingFormName: string }> = {
  type: GET_DOCUMENT,
  hideLoadIndicator: true,
  getData: ({ id }) => DocumentsService.getDocumentItem(id),
  processData: (editingDocument: Document, state: any, { editingFormName }) => [
      {
        type: SET_EDITING_DOCUMENT,
        payload: { editingDocument, editingFormName }
      }
    ]
};

export const EpicGetDocumentByID: Epic<any, any> = EpicUtils.Create(request);
