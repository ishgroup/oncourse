import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import InvoicesEditView from "../../../js/containers/entities/invoices/components/InvoicesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered InvoicesEditView", () => {
  mockedEditView({
    entity: "Invoice",
    EditView: InvoicesEditView,
    record: mockecApi => mockecApi.db.getInvoice(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#contactId").text()).toContain(initialValues.contactName);
      expect(wrapper.find("#customerReference").text()).toContain("No value");
      expect(wrapper.find("#invoiceNumber").text()).toContain(initialValues.invoiceNumber);

      expect(wrapper.find("#invoiceDate").text()).toContain(
        format(new Date(initialValues.invoiceDate), III_DD_MMM_YYYY).toString()
      );
      expect(wrapper.find("#dateDue").text()).toContain("17 Apr 2018");

      expect(wrapper.find("#billToAddress").text()).toContain("No value");
      expect(wrapper.find("#shippingAddress").text()).toContain("No value");
      expect(wrapper.find("#publicNotes").text()).toContain("No value");
      expect(wrapper.find('input[type="checkbox"]').props().checked).toEqual(false);
    }
  });
});
