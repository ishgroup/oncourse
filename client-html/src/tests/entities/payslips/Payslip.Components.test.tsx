import PayslipsEditView from "../../../js/containers/entities/payslips/components/PayslipsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PayslipsEditView", () => {
  mockedEditView({
    entity: "Payslip",
    EditView: PayslipsEditView,
    record: mockecApi => mockecApi.db.getPayslip(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#tutorId input").val()).toContain(initialValues.tutorFullName);
      expect(wrapper.find("#tags input").val()).toContain("No value");
      expect(wrapper.find("#publicNotes input").val()).toContain("No value");
      expect(wrapper.find("#privateNotes input").val()).toContain("No value");
    }
  });
});
