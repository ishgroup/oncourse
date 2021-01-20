import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  getContraInvoices,
  SET_CONTRA_INVOICES
} from "../../../js/containers/entities/invoices/actions";
import { ContraInvoice, ContraInvoiceFormData } from "../../../js/containers/entities/invoices/reducers/state";
import { EpicGetContraInvoices } from "../../../js/containers/entities/invoices/epics/EpicGetContraInvoices";

const savedInvoiceToContra = {
  id: 10,
  contactId: 1,
  contactName: "lastName 2 firstName2",
  amountTotal: -132,
  amountLeft: -132,
  contraInvoices: []
};

describe("Get contra invoices epic tests", () => {
  it("EpicGetContraInvoices should returns correct values", () => DefaultEpic({
    action: getContraInvoices(savedInvoiceToContra),
    epic: EpicGetContraInvoices,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainInvoices({
        columns: "dateDue,invoiceNumber,amountOwing",
        search: "contact.id == 1 and amountOwing > 0"
      });

      let contraInvoices: ContraInvoice[] = null;
      let invoiceToContra: ContraInvoiceFormData = null;

      if (response) {
        invoiceToContra = savedInvoiceToContra;

        if (response.rows.length) {
          contraInvoices = response.rows.map(({ id, values }) => ({
            id: Number(id),
            includeInPayment: false,
            dueDate: values[0],
            invoiceNumber: Number(values[1]),
            owing: Number(values[2]),
            toBePaid: 0
          }));
        }
      }

      return [
        {
          type: SET_CONTRA_INVOICES,
          payload: { contraInvoices }
        },

        ...(contraInvoices && invoiceToContra
          ? [initialize("ContraInvoiceForm", { ...invoiceToContra, contraInvoices })]
          : [])
      ];
    }
  }));
});
