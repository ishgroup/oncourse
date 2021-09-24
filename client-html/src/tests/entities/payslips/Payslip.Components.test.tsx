import PayslipsEditView from "../../../js/containers/entities/payslips/components/PayslipsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PayslipsEditView", () => {
  mockedEditView({
    entity: "Payslip",
    EditView: PayslipsEditView,
    record: mockecApi => mockecApi.db.getPayslip(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#tutorId input").val()).toContain(initialValues.tutorFullName);
    }
  });
});
