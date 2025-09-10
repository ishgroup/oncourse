import { formatToDateOnly } from 'ish-ui';
import { initialize } from 'redux-form';
import { setListFullScreenEditView } from '../../../js/common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../js/common/components/list-view/constants';
import { duplicateAndReverseInvoice } from '../../../js/containers/entities/invoices/actions';
import { DefaultEpic } from '../../common/Default.Epic';

describe("Duplicate and reverse invoice epic tests", () => {
  it("EpicDuplicateAndReverseInvoice should returns correct values", async () => {

    const { EpicDuplicateAndReverseInvoice } = await import('../../../js/containers/entities/invoices/epics/EpicDuplicateAndReverseInvoice');

    return DefaultEpic({
      action: duplicateAndReverseInvoice(1),
      epic: EpicDuplicateAndReverseInvoice,
      processData: mockedApi => {
        const data = mockedApi.db.getInvoice(1);
        data.invoiceLines.forEach(l => {
          l.priceEachExTax = -l.priceEachExTax;
          l.discountEachExTax = -l.discountEachExTax;
          l.taxEach = -l.taxEach;
          l.id = null;
        });

        data.paymentPlans = [data.paymentPlans[0]];

        data.paymentPlans[0].amount = -data.paymentPlans[0].amount;
        data.quoteNumber = null;

        data.total = -data.total;
        data.amountOwing = data.total;
        data.invoiceDate = formatToDateOnly(new Date());
        data.dateDue = formatToDateOnly(new Date());
        data.overdue = 0;
        data.id = null;
        data.invoiceNumber = null;

        return [
          setListFullScreenEditView(true),
          initialize(LIST_EDIT_VIEW_FORM_NAME, data)
        ];
      }
    })
  });
});
