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
      expect(wrapper.find("#studyReason input").val()).toContain(initialValues.studyReason);
      expect(wrapper.find("#vetFeeExemptionType input").val()).toContain(initialValues.vetFeeExemptionType);
      expect(wrapper.find("#fundingSource input").val()).toContain(initialValues.fundingSource);

      expect(wrapper.find("#creditLevel input").val()).toContain(initialValues.creditLevel);
    }
  });
});
