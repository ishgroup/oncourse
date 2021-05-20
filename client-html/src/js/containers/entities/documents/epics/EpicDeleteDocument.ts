import { Epic } from "redux-observable";
import { Diff } from "@api/model";
import { showMessage } from "../../../../common/actions";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_DOCUMENT_ITEM } from "../actions";

const request: EpicUtils.Request<any,  Diff> = {
  type: DELETE_DOCUMENT_ITEM,
  hideLoadIndicator: true,
  getData: diff => DocumentsService.bulkChange(diff),
  processData: () => [
    showMessage({ message: "Document record moved to bin", success: true }),
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Document", listUpdate: true }
    },
  ]
};

export const EpicDeleteDocument: Epic<any, any> = EpicUtils.Create(request);
