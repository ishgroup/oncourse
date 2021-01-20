import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetAmountOwing } from "../../../js/containers/entities/invoices/epics/EpicGetAmountOwing";
import {
  GET_AMOUNT_OWING_FULFILLED,
  GET_CONTRA_INVOICES,
  getAmountOwing
} from "../../../js/containers/entities/invoices/actions";
import { ContraInvoiceFormData } from "../../../js/containers/entities/invoices/reducers/state";

describe("Get amount owing epic tests", () => {
  it("EpicGetAmountOwing should returns correct values", () => DefaultEpic({
    action: getAmountOwing(1),
    epic: EpicGetAmountOwing,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainInvoices({
        columns: "dateDue,invoiceNumber,amountOwing",
        search: "id == 1"
      });

      let invoiceToContra: ContraInvoiceFormData = null;

      const { id, values } = response.rows[0];

      const selectedInvoiceAmountOwing = parseFloat(values[3]);

      if (selectedInvoiceAmountOwing < 0) {
        const parsedAmount = -selectedInvoiceAmountOwing;

        invoiceToContra = {
          id: Number(id),
          contactId: Number(values[0]),
          contactName: values[1] ? values[1] + (values[2] && values[2] !== values[1] ? `, ${values[2]}` : "") : "",
          amountTotal: parsedAmount,
          amountLeft: parsedAmount,
          contraInvoices: []
        };
      }

      return [
        {
          type: GET_AMOUNT_OWING_FULFILLED,
          payload: { selectedInvoiceAmountOwing }
        },
        ...(invoiceToContra
          ? [
            {
              type: GET_CONTRA_INVOICES,
              payload: invoiceToContra
            }
          ]
          : [])
      ];
    }
  }));
});
