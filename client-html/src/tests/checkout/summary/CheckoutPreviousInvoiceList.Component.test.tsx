import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import CheckoutPreviousInvoiceList
  from "../../../js/containers/checkout/components/summary/CheckoutPreviousInvoiceList";
import { CheckoutPage, titles } from "../../../js/containers/checkout/constants";
import { decimalPlus } from "../../../js/common/utils/numbers/decimalCalculation";

describe("Virtual rendered CheckoutPreviousInvoiceList", () => {
  defaultComponents({
    entity: "CheckoutPreviousInvoiceList",
    View: props => <CheckoutPreviousInvoiceList {...props} />,
    record: mockedApi => mockedApi.db.getAmountOwing(true),
    defaultProps: ({ initialValues }) => {
      let invoiceTotal = 0;

      const updatedValues = initialValues.map(item => {
        invoiceTotal = decimalPlus(invoiceTotal, parseFloat(item.amountOwing));
        return { ...item, checked: true };
      });

      const previousInvoices = {
        invoices: updatedValues,
        invoiceTotal,
        unCheckAll: false
      };

      return {
        form: "CheckoutPreviousInvoiceList",
        titles,
        initialValues: previousInvoices,
        values: previousInvoices,
        activeField: CheckoutPage.previousOwing,
        previousInvoices,
        toggleInvoiceItem: jest.fn(),
        uncheckAllPreviousInvoice: jest.fn()
      };
    },
    render: (wrapper, initialValues) => {
      const count = initialValues.length;
      let i;

      expect(wrapper.find("input[type='checkbox']").at(0).props().checked).toEqual(false);

      for (i = 1; i <= count; i++) {
        expect(wrapper.find("input[type='checkbox']").at(i).props().checked).toEqual(true);
      }
    }
  });
});
