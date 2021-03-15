import EnrolmentEditView from "../../../js/containers/entities/enrolments/components/EnrolmentEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered EnrolmentEditView", () => {
  mockedEditView({
    entity: "Enrolment",
    EditView: EnrolmentEditView,
    record: mockecApi => mockecApi.db.getEnrolment(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#studentContactId").text()).toContain(initialValues.studentName);
      expect(wrapper.find("#displayStatus").text()).toContain(initialValues.displayStatus);
      expect(wrapper.find("#source").text()).toContain(initialValues.source);
      // expect(wrapper.find("div[id='relatedFundingSourceId']").text()).toContain("No value");
      expect(wrapper.find("#studyReason").text()).toContain(initialValues.studyReason);
      expect(wrapper.find("#vetFeeExemptionType").text()).toContain(initialValues.vetFeeExemptionType);
      expect(wrapper.find("#fundingSource").text()).toContain(initialValues.fundingSource);
      expect(wrapper.find("#vetFundingSourceStateID").text()).toContain("No value");
      expect(wrapper.find('#General input[type="checkbox"]').at(0).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(1).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(2).props().checked).toEqual(false);
      expect(wrapper.find("#associatedCourseIdentifier").text()).toContain("No value");
      expect(wrapper.find("#vetPurchasingContractID").text()).toContain("No value");
      expect(wrapper.find("#outcomeIdTrainingOrg").text()).toContain("No value");
      expect(wrapper.find("#vetClientID").text()).toContain("No value");
      expect(wrapper.find("#vetTrainingContractID").text()).toContain("No value");
      expect(wrapper.find("#cricosConfirmation").text()).toContain("No value");
      expect(wrapper.find('#General input[type="checkbox"]').at(3).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(4).props().checked).toEqual(false);
      expect(wrapper.find('#General input[type="checkbox"]').at(5).props().checked).toEqual(false);

      expect(wrapper.find("#feeHelpAmount").text()).toContain("null0.00");
      expect(wrapper.find("#feeStatus").text()).toContain("No value");
      expect(wrapper.find("#attendanceType").text()).toContain("No value");
      expect(wrapper.find("#creditOfferedValue").text()).toContain("No value");
      expect(wrapper.find("#creditUsedValue").text()).toContain("No value");
      expect(wrapper.find("#creditTotal").text()).toContain("No value");
      expect(wrapper.find("#creditFOEId").text()).toContain("No value");
      expect(wrapper.find("#creditProvider").text()).toContain("No value");
      expect(wrapper.find("#creditProviderType").text()).toContain("No value");
      expect(wrapper.find("#creditType").text()).toContain("No value");
      expect(wrapper.find("#creditLevel").text()).toContain(initialValues.creditLevel);
    }
  });
});
