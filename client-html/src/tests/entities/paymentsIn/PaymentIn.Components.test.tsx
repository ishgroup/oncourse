import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "ish-ui";
import PaymentInsEditView from "../../../js/containers/entities/paymentsIn/components/PaymentInEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PaymentInsEditView", () => {
  mockedEditView({
    entity: "PaymentIn",
    EditView: PaymentInsEditView,
    record: mockecApi => mockecApi.db.getPaymentIn(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        administrationCenterId: initialValues.administrationCenterName,
      });

      expect(screen.getByLabelText('Payment from', { selector: 'input' }).value).toBe(initialValues.payerName);
      expect(screen.getByLabelText('Type').value).toBe(initialValues.paymentInType);
      expect(screen.getByLabelText('Status').value).toBe(initialValues.status);
      expect(screen.getByLabelText('Amount').value).toBe(initialValues.amount.toFixed(2));
      expect(screen.getByLabelText('Account').value).toBe(initialValues.accountInName);
      expect(screen.getByLabelText('Source').value).toBe(initialValues.source);
      expect(screen.getByLabelText('CC transaction').value).toBe(initialValues.ccTransaction);
      expect(screen.getByLabelText('Date paid').value).toBe(format(new Date(initialValues.datePayed), III_DD_MMM_YYYY).toString());
      expect(screen.getByLabelText('Created by').value).toBe(initialValues.createdBy);
    }
  });
});
