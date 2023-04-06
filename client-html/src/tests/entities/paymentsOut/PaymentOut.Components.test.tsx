import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import PaymentsOutEditView from "../../../js/containers/entities/paymentsOut/components/PaymentOutEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PaymentsOutEditView", () => {
  mockedEditView({
    entity: "PaymentOut",
    EditView: PaymentsOutEditView,
    record: mockecApi => mockecApi.db.getPaymentOut(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        administrationCenterId: initialValues.administrationCenterName,
        privateNotes: initialValues.privateNotes,
      });

      expect(screen.getByLabelText('Payment to', { selector: 'input' }).value).toBe(initialValues.payeeName);
      expect(screen.getByLabelText('Status').value).toBe(initialValues.status);
      expect(screen.getByLabelText('Account').value).toBe("Account is disabled");
      expect(screen.getByLabelText('Amount').value).toBe(initialValues.amount.toFixed(2));
      expect(screen.getByLabelText('Date paid').value).toBe(format(new Date(initialValues.datePayed), III_DD_MMM_YYYY).toString());
      expect(screen.getByLabelText('Date banked').value).toBe(format(new Date(initialValues.dateBanked), III_DD_MMM_YYYY).toString());
      expect(screen.getByLabelText('Created by').value).toBe(initialValues.createdBy);
    }
  });
});
