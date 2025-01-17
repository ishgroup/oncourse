/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import { DocumentSearchItemType } from "../../../../../model/entities/Document";
import * as EpicUtils from "../../../../epics/EpicUtils";
import EntityService from "../../../../services/EntityService";
import { SEARCH_DOCUMENT_BY_NAME, setSearchDocuments } from "../actions";

const defaultDocumentMap = ({ id, values }): DocumentSearchItemType => ({
  id: Number(id),
  link: values[0],
  name: values[1],
  added: values[2],
  byteSize: values[3],
  fileName: values[4],
  isShared: JSON.parse(values[5]),
  attachedRecordsCount: JSON.parse(values[6])
});

const request: EpicUtils.Request<any, { documentName: string; editingFormName: string }> = {
  type: SEARCH_DOCUMENT_BY_NAME,
  hideLoadIndicator: true,
  getData: ({ documentName }) => EntityService.getPlainRecords(
    "Document",
    "link,name,added,currentVersion.byteSize,currentVersion.fileName,isShared,attachedRecordsCount",
    `~ "${documentName}" and isRemoved = false`
  ),
  processData: (plainRecords: DataResponse) => {
    const searchDocuments: DocumentSearchItemType[] = plainRecords.rows.map(defaultDocumentMap)
      .filter(r => r.isShared || r.attachedRecordsCount === 0);
    return [
      setSearchDocuments(searchDocuments)
    ];
  }
};

export const EpicSearchExistingDocumentByName: Epic<any, any> = EpicUtils.Create(request);
