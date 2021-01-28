import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicPostContraInvoices } from "../../../js/containers/entities/invoices/epics/EpicPostContraInvoices";
import { POST_CONTRA_INVOICES_FULFILLED, postContraInvoices } from "../../../js/containers/entities/invoices/actions";

describe("Submit contra invoices epic tests", () => {
  it("EpicPostContraInvoices should returns correct values", () => DefaultEpic({
    action: postContraInvoices(1, [323]),
    epic: EpicPostContraInvoices,
    processData: () => [
      {
        type: POST_CONTRA_INVOICES_FULFILLED
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Invoice", listUpdate: true }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Invoice owing updated" }
      }
    ]
  }));
});
