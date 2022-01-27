import PayslipsEditView from "../../../js/containers/entities/payslips/components/PayslipsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PayslipsEditView", () => {
  mockedEditView({
    entity: "Payslip",
    EditView: PayslipsEditView,
    record: mockecApi => mockecApi.db.getPayslip(1),
    render: ({ screen, initialValues, formRoleName }) => {
      const paylines = {};

      initialValues.paylines.forEach((payline, i) => {
        paylines[`paylines[${i}].value`] = payline.value.toString();
        paylines[`paylines[${i}].description`] = payline.description;
      });

      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        tutorId: initialValues.tutorFullName,
        payType: initialValues.payType,
        publicNotes: initialValues.publicNotes,
        privateNotes: initialValues.privateNotes,
        ...paylines,
      });
    }
  });
});
