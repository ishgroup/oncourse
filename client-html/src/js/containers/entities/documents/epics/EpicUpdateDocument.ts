import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_DOCUMENT_EDIT, UPDATE_DOCUMENT_ITEM, UPDATE_DOCUMENT_ITEM_FULFILLED } from "../actions";

const request: EpicUtils.Request<any, { id: number; document: Document & { notes: any } }> = {
  type: UPDATE_DOCUMENT_ITEM,
  getData: ({ id, document }) => DocumentsService.updateDocumentItem(id, document),
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
    {
      type: UPDATE_DOCUMENT_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Document Record updated" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Document", listUpdate: true, savedID: id }
    },
    ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
      type: GET_DOCUMENT_EDIT,
      payload: id
    }] : []
  ],
  processError: (response, { document }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, document)]
};

export const EpicUpdateDocumentItem: Epic<any, any> = EpicUtils.Create(request);
