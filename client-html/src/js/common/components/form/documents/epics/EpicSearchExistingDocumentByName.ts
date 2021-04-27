/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse, Document } from "@api/model";
import * as EpicUtils from "../../../../epics/EpicUtils";
import { SEARCH_DOCUMENT_BY_NAME, SET_SEARCH_DOCUMENTS } from "../actions";
import EntityService from "../../../../services/EntityService";

const defaultDocumentMap = ({ id, values }): DocumentSearchItem => ({
  id: Number(id),
  link: values[0],
  name: values[1],
  added: values[2],
  byteSize: values[3],
  fileName: values[4],
  isShared: JSON.parse(values[5]),
  attachedRecordsCount: JSON.parse(values[6])
});

export interface DocumentSearchItem {
  id: number;
  link: string;
  name: string;
  added: string;
  byteSize: string;
  fileName: string;
  isShared: boolean;
  attachedRecordsCount: number;
}

const request: EpicUtils.Request<any, { documentName: string; editingFormName: string }> = {
  type: SEARCH_DOCUMENT_BY_NAME,
  hideLoadIndicator: true,
  getData: ({ documentName }) => EntityService.getPlainRecords(
      "Document",
      "link,name,added,currentVersion.byteSize,currentVersion.fileName,isShared,attachedRecordsCount",
      `~ "${documentName}" and isRemoved = false`
    ),
  processData: (plainRecords: DataResponse, state: any, { editingFormName }) => {
    const searchDocuments: DocumentSearchItem[] = plainRecords.rows.map(defaultDocumentMap)
        .filter(r => r.isShared || r.attachedRecordsCount === 0);
    return [
      {
        type: SET_SEARCH_DOCUMENTS,
        payload: { searchDocuments, editingFormName }
      }
    ];
  }
};

export const EpicSearchExistingDocumentByName: Epic<any, any> = EpicUtils.Create(request);
