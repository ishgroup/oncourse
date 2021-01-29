/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Diff, Document } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { RESTORE_DOCUMENT } from "../actions";
import { showMessage } from "../../../../common/actions";

const request: EpicUtils.Request<any, any, Diff> = {
  type: RESTORE_DOCUMENT,
  hideLoadIndicator: true,
  getData: diff => DocumentsService.bulkChange(diff),
  processData: () => [
    showMessage({ message: "Document record restored", success: true }),
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Document", listUpdate: true }
    },
  ]
};

export const EpicRestoreDocument: Epic<any, any> = EpicUtils.Create(request);
