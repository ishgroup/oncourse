import OutcomeEditView from "../../../js/containers/entities/outcomes/components/OutcomeEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered OutcomeEditView", () => {
  mockedEditView({
    entity: "Outcome",
    EditView: OutcomeEditView,
    record: mockecApi => mockecApi.db.getOutcome(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find(".textField").at(0).text()).toContain(initialValues.studentName);
      expect(wrapper.find("#moduleCode").text()).toContain(initialValues.moduleCode);
      expect(wrapper.find("#moduleName").text()).toContain(initialValues.moduleName);

      expect(wrapper.find("#trainingPlanStartDate").text()).toContain("No value");
      expect(wrapper.find("#trainingPlanEndDate").text()).toContain("No value");
      expect(wrapper.find("#actualStartDate").text()).toContain("No value");
      expect(wrapper.find("#actualEndDate").text()).toContain("No value");

      expect(wrapper.find("#deliveryMode").text()).toContain(initialValues.deliveryMode);
      expect(wrapper.find("#reportableHours").text()).toContain(initialValues.reportableHours);
      expect(wrapper.find("#fundingSource").text()).toContain(initialValues.fundingSource);
      expect(wrapper.find("#status").text()).toContain("No value");
      expect(wrapper.find("#hoursAttended").text()).toContain(initialValues.hoursAttended);
      expect(wrapper.find("#vetFundingSourceStateID").text()).toContain("No value");
      expect(wrapper.find("#vetPurchasingContractID").text()).toContain("No value");
      expect(wrapper.find("#vetPurchasingContractScheduleID").text()).toContain("No value");
      expect(wrapper.find("#specificProgramIdentifier").text()).toContain(initialValues.specificProgramIdentifier);
    }
  });
});
