import OutcomeEditView from "../../../js/containers/entities/outcomes/components/OutcomeEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered OutcomeEditView", () => {
  mockedEditView({
    entity: "Outcome",
    EditView: OutcomeEditView,
    record: mockecApi => mockecApi.db.getOutcome(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        moduleCode: initialValues.moduleCode,
        moduleName: initialValues.moduleName,
        deliveryMode: initialValues.deliveryMode,
        reportableHours: initialValues.reportableHours,
        fundingSource: initialValues.fundingSource,
        status: initialValues.status,
        hoursAttended: initialValues.hoursAttended,
        vetFundingSourceStateID: initialValues.vetFundingSourceStateID,
        vetPurchasingContractID: initialValues.vetPurchasingContractID,
        vetPurchasingContractScheduleID: initialValues.vetPurchasingContractScheduleID,
        specificProgramIdentifier: initialValues.specificProgramIdentifier,
      });
    }
  });
});
