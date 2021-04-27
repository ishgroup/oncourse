/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";

import { Document } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { GET_DOCUMENT_EDIT, GET_DOCUMENT_EDIT_FULFILLED } from "../actions";

const request: EpicUtils.Request<any, number> = {
  type: GET_DOCUMENT_EDIT,
  hideLoadIndicator: true,
  getData: id => DocumentsService.getDocumentItem(id),
  processData: (document: Document) => [
      {
        type: GET_DOCUMENT_EDIT_FULFILLED,
        payload: { document }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: document, name: document.name }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, document)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetDocument: Epic<any, any> = EpicUtils.Create(request);
