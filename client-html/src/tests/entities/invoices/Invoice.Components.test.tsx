import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import { AccountDefaultInvoiceLine } from "../../../js/model/preferences";
import InvoicesEditView from "../../../js/containers/entities/invoices/components/InvoicesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered InvoicesEditView", () => {
  mockedEditView({
    entity: "Invoice",
    EditView: InvoicesEditView,
    record: mockecApi => mockecApi.db.getInvoice(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByLabelText('Overdue').value).toBe(initialValues.overdue.toFixed(2));
      expect(screen.getByLabelText('Source').value).toBe(initialValues.source);
      expect(screen.getByLabelText('Created by').value).toBe(initialValues.createdByUser);

      setTimeout(() => {
        expect(screen.getByRole(formRoleName)).toHaveFormValues({
          title: initialValues.title,
          leadId: initialValues.leadCustomerName,
          contactId: initialValues.contactName,
          customerReference: initialValues.customerReference,
          invoiceNumber: initialValues.invoiceNumber.toString(),
          invoiceDate: format(new Date(initialValues.invoiceDate), III_DD_MMM_YYYY).toString(),
          dateDue: format(new Date(initialValues.paymentPlans.find(p => p.id === 517).date), III_DD_MMM_YYYY).toString(),
          billToAddress: initialValues.billToAddress,
          shippingAddress: initialValues.shippingAddress,
          description: initialValues.description,
          publicNotes: initialValues.publicNotes,
          sendEmail: initialValues.sendEmail,
        });
      }, 500);
    },
    state: () => ({
      preferences: {
        financial: {
          [AccountDefaultInvoiceLine.uniqueKey]: 0
        }
      }
    })
  });
});
