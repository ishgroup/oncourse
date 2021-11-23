import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import InvoicesEditView from "../../../js/containers/entities/invoices/components/InvoicesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

// TODO Enable test on fix

describe("Virtual rendered InvoicesEditView", () => {
  mockedEditView({
    entity: "Invoice",
    EditView: InvoicesEditView,
    record: mockecApi => mockecApi.db.getInvoice(1),
    render: (wrapper, initialValues) => {
      // expect(wrapper.find("#contactId input").val()).toContain(initialValues.contactName);
      // expect(wrapper.find("#invoiceDate input").val()).toContain(
      //   format(new Date(initialValues.invoiceDate), III_DD_MMM_YYYY).toString()
      // );
      // expect(wrapper.find("#dateDue input").val()).toContain("17 Apr 2018");
    }
  });
});
