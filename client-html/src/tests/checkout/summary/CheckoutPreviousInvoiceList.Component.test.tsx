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
        unCheckAll: false,
        payDueAmounts: true
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
    render: (wrapper, initialValues, shallow) => {
      const count = initialValues.length;
      let i;


      expect(shallow.find("input[type='checkbox']").props().checked).toEqual(true);

      for (i = 1; i <= count; i++) {
        expect(shallow.find("input[type='checkbox']").props().checked).toEqual(true);
      }
    }
  });
});
