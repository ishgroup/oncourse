import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_DEFAULT_INVOICE_TERMS_FULFILLED,
  getDefaultInvoiceTerms
} from "../../../js/containers/entities/invoices/actions";
import { EpicGetDefaultInvoiceTerms } from "../../../js/containers/entities/invoices/epics/EpicGetDefaultInvoiceTerms";
import { ACCOUNT_INVOICE_TERMS } from "../../../js/constants/Config";

describe("Get default invoice terms epic tests", () => {
  it("EpicGetDefaultInvoiceTerms should returns correct values", () => DefaultEpic({
    action: getDefaultInvoiceTerms(),
    epic: EpicGetDefaultInvoiceTerms,
    processData: mockedApi => {
      const response = mockedApi.db.getUserPreferences([ACCOUNT_INVOICE_TERMS]);
      return [
        {
          type: GET_DEFAULT_INVOICE_TERMS_FULFILLED,
          payload: { defaultTerms: response[ACCOUNT_INVOICE_TERMS] }
        }
      ];
    }
  }));
});
