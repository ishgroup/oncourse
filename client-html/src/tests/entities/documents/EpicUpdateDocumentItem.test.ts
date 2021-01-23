import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateDocumentItem } from "../../../js/containers/entities/documents/epics/EpicUpdateDocument";
import {
  GET_DOCUMENT_EDIT,
  UPDATE_DOCUMENT_ITEM_FULFILLED,
  updateDocument
} from "../../../js/containers/entities/documents/actions";

describe("Update document epic tests", () => {
  it("EpicUpdateDocumentItem should returns correct values", () => {
    const id = "2";
    return DefaultEpic({
      action: mockedApi => updateDocument("2", mockedApi.db.getDiscount(Number(id))),
      epic: EpicUpdateDocumentItem,
      processData: () => [
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
        {
          type: GET_DOCUMENT_EDIT,
          payload: id
        }
      ]
    });
  });
});
