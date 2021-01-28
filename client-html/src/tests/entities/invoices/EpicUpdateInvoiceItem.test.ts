import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateInvoiceItem } from "../../../js/containers/entities/invoices/epics/EpicUpdateInvoiceItem";
import {
  GET_INVOICE_ITEM,
  UPDATE_INVOICE_ITEM_FULFILLED,
  updateInvoice
} from "../../../js/containers/entities/invoices/actions";

describe("Update invoice epic tests", () => {
  it("EpicUpdateInvoiceItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateInvoice("1", mockedApi.db.getInvoice(1)),
    epic: EpicUpdateInvoiceItem,
    processData: () => [
      {
        type: UPDATE_INVOICE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Invoice Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Invoice", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_INVOICE_ITEM,
        payload: "1"
      }
    ]
  }));
});
