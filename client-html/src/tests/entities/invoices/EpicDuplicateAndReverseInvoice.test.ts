import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { duplicateAndReverseInvoice } from "../../../js/containers/entities/invoices/actions";
import { EpicDuplicateAndReverseInvoice } from "../../../js/containers/entities/invoices/epics/EpicDuplicateAndReverseInvoice";
import { formatToDateOnly } from "../../../js/common/utils/dates/datesNormalizing";

describe("Duplicate and reverse invoice epic tests", () => {
  it("EpicDuplicateAndReverseInvoice should returns correct values", () => DefaultEpic({
    action: duplicateAndReverseInvoice(1),
    epic: EpicDuplicateAndReverseInvoice,
    processData: mockedApi => {
      const data = mockedApi.db.getInvoice(1);
      data.invoiceLines.forEach(l => {
        l.priceEachExTax = -l.priceEachExTax;
        l.discountEachExTax = -l.discountEachExTax;
        l.taxEach = -l.taxEach;
      });

      data.paymentPlans = [data.paymentPlans[0]];

      data.paymentPlans[0].amount = -data.paymentPlans[0].amount;

      data.total = -data.total;
      data.amountOwing = data.total;
      data.invoiceDate = formatToDateOnly(new Date());
      data.dateDue = formatToDateOnly(new Date());
      data.overdue = 0;
      data.id = null;
      data.invoiceNumber = null;

      return [initialize(LIST_EDIT_VIEW_FORM_NAME, data)];
    }
  }));
});
