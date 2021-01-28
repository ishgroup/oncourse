import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetInvoice } from "../../../js/containers/entities/invoices/epics/EpicGetInvoice";
import { GET_INVOICE_ITEM_FULFILLED, getInvoice } from "../../../js/containers/entities/invoices/actions";
import { getInvoiceClosestPaymentDueDate, sortInvoicePaymentPlans } from "../../../js/containers/entities/invoices/utils";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get invoice epic tests", () => {
  it("EpicGetInvoice should returns correct values", () => DefaultEpic({
    action: getInvoice("1"),
    epic: EpicGetInvoice,
    processData: mockedApi => {
      const invoice = mockedApi.db.getInvoice(1);
      invoice.paymentPlans.sort(sortInvoicePaymentPlans);
      getInvoiceClosestPaymentDueDate(invoice);

      return [
        {
          type: GET_INVOICE_ITEM_FULFILLED,
          payload: { invoice }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: invoice, name: invoice.invoiceNumber }
        },
        getNoteItems("Invoice", "1" as any, LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, invoice)
      ];
    }
  }));
});
