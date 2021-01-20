import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateInvoice } from "../../../js/containers/entities/invoices/epics/EpicCreateInvoice";
import { CREATE_INVOICE_ITEM_FULFILLED, createInvoice } from "../../../js/containers/entities/invoices/actions";

describe("Create invoice epic tests", () => {
  it("EpicCreateInvoice should returns correct values", () => DefaultEpic({
    action: mockedApi => createInvoice({ ...mockedApi.db.getInvoice(1), id: "" }),
    epic: EpicCreateInvoice,
    processData: () => [
      {
        type: CREATE_INVOICE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Invoice Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Invoice", listUpdate: true }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
