import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_DOCUMENT_ITEM, DELETE_DOCUMENT_ITEM_FULFILLED } from "../actions";

const request: EpicUtils.Request<any, any, any> = {
  type: DELETE_DOCUMENT_ITEM,
  getData: (id: number) => DocumentsService.deleteDocumentItem(id),
  processData: () => [
    {
      type: DELETE_DOCUMENT_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Document record deleted" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Document", listUpdate: true }
    },
    setListSelection([]),
    initialize(LIST_EDIT_VIEW_FORM_NAME, null)
  ],
  processError: response => FetchErrorHandler(response, "Document record was not deleted")
};

export const EpicDeleteDocument: Epic<any, any> = EpicUtils.Create(request);