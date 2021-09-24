import OutcomeEditView from "../../../js/containers/entities/outcomes/components/OutcomeEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered OutcomeEditView", () => {
  mockedEditView({
    entity: "Outcome",
    EditView: OutcomeEditView,
    record: mockecApi => mockecApi.db.getOutcome(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#moduleCode input").val()).toContain(initialValues.moduleCode);
      expect(wrapper.find("#moduleName input").val()).toContain(initialValues.moduleName);
      
      expect(wrapper.find("#deliveryMode input").val()).toContain(initialValues.deliveryMode);
      expect(wrapper.find("#reportableHours input").val()).toContain(initialValues.reportableHours);
      expect(wrapper.find("#fundingSource input").val()).toContain(initialValues.fundingSource);
      expect(wrapper.find("#hoursAttended input").val()).toContain(initialValues.hoursAttended);
      expect(wrapper.find("#specificProgramIdentifier input").val()).toContain(initialValues.specificProgramIdentifier);
    }
  });
});
