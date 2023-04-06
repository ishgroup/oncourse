import EnrolmentEditView from "../../../js/containers/entities/enrolments/components/EnrolmentEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered EnrolmentEditView", () => {
  mockedEditView({
    entity: "Enrolment",
    EditView: EnrolmentEditView,
    record: mockecApi => mockecApi.db.getEnrolment(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        displayStatus: initialValues.displayStatus,
        source: initialValues.source,
        studyReason: initialValues.studyReason,
        vetFeeExemptionType: initialValues.vetFeeExemptionType,
        fundingSource: initialValues.fundingSource,
        vetFundingSourceStateID: initialValues.vetFundingSourceStateID,
        vetIsFullTime: initialValues.vetIsFullTime,
        vetInSchools: initialValues.vetInSchools,
        suppressAvetmissExport: initialValues.suppressAvetmissExport,
        associatedCourseIdentifier: initialValues.associatedCourseIdentifier,
        vetPurchasingContractID: initialValues.vetPurchasingContractID,
        outcomeIdTrainingOrg: initialValues.outcomeIdTrainingOrg,
        vetClientID: initialValues.vetClientID,
        vetTrainingContractID: initialValues.vetTrainingContractID,
        cricosConfirmation: initialValues.cricosConfirmation,
        eligibilityExemptionIndicator: initialValues.eligibilityExemptionIndicator,
        vetFeeIndicator: initialValues.vetFeeIndicator,
        trainingPlanDeveloped: initialValues.trainingPlanDeveloped,
        studentLoanStatus: initialValues.studentLoanStatus,
        creditOfferedValue: initialValues.creditOfferedValue,
        creditUsedValue: initialValues.creditUsedValue,
        creditTotal: initialValues.creditTotal,
        creditFOEId: initialValues.creditFOEId,
        creditProvider: initialValues.creditProvider,
        creditProviderType: initialValues.creditProviderType,
        creditType: initialValues.creditType,
        creditLevel: initialValues.creditLevel,
      });
    }
  });
});
