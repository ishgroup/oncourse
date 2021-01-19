import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteDocument } from "../../../js/containers/entities/documents/epics/EpicDeleteDocument";
import { DELETE_DOCUMENT_ITEM_FULFILLED, removeDocument } from "../../../js/containers/entities/documents/actions";

describe("Delete document epic tests", () => {
  it("EpicDeleteDocument should returns correct values", () => DefaultEpic({
    action: removeDocument("1"),
    epic: EpicDeleteDocument,
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
    ]
  }));
});
