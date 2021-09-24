import EnrolmentEditView from "../../../js/containers/entities/enrolments/components/EnrolmentEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered EnrolmentEditView", () => {
  mockedEditView({
    entity: "Enrolment",
    EditView: EnrolmentEditView,
    record: mockecApi => mockecApi.db.getEnrolment(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#studentContactId input").val()).toContain(initialValues.studentName);
      expect(wrapper.find("#displayStatus input").val()).toContain(initialValues.displayStatus);
      expect(wrapper.find("#source input").val()).toContain(initialValues.source);
      // expect(wrapper.find("div[id='relatedFundingSourceId'] input").val()).toContain("No value");
      expect(wrapper.find("#studyReason input").val()).toContain(initialValues.studyReason);
      expect(wrapper.find("#vetFeeExemptionType input").val()).toContain(initialValues.vetFeeExemptionType);
      expect(wrapper.find("#fundingSource input").val()).toContain(initialValues.fundingSource);
      expect(wrapper.find("#vetFundingSourceStateID input").val()).toContain("No value");
      expect(wrapper.find('#General input[type="checkbox"]').at(0).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(1).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(2).props().checked).toEqual(false);
      expect(wrapper.find("#associatedCourseIdentifier input").val()).toContain("No value");
      expect(wrapper.find("#vetPurchasingContractID input").val()).toContain("No value");
      expect(wrapper.find("#outcomeIdTrainingOrg input").val()).toContain("No value");
      expect(wrapper.find("#vetClientID input").val()).toContain("No value");
      expect(wrapper.find("#vetTrainingContractID input").val()).toContain("No value");
      expect(wrapper.find("#cricosConfirmation input").val()).toContain("No value");
      expect(wrapper.find('#General input[type="checkbox"]').at(3).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(4).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(5).props().checked).toEqual(false);

      expect(wrapper.find("#feeHelpAmount input").val()).toContain("null0.00");
      expect(wrapper.find("#feeStatus input").val()).toContain("No value");
      expect(wrapper.find("#attendanceType input").val()).toContain("No value");
      expect(wrapper.find("#creditOfferedValue input").val()).toContain("No value");
      expect(wrapper.find("#creditUsedValue input").val()).toContain("No value");
      expect(wrapper.find("#creditTotal input").val()).toContain("No value");
      expect(wrapper.find("#creditFOEId input").val()).toContain("No value");
      expect(wrapper.find("#creditProvider input").val()).toContain("No value");
      expect(wrapper.find("#creditProviderType input").val()).toContain("No value");
      expect(wrapper.find("#creditType input").val()).toContain("No value");
      expect(wrapper.find("#creditLevel input").val()).toContain(initialValues.creditLevel);
    }
  });
});
