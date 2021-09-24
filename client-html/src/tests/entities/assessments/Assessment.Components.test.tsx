import AssessmentEditView from "../../../js/containers/entities/assessments/components/AssessmentEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered AssessmentEditView", () => {
  mockedEditView({
    entity: "Assessment",
    EditView: AssessmentEditView,
    record: mockecApi => mockecApi.db.getAssessment(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find('input[type="checkbox"]').props().checked).toEqual(initialValues.active);
      expect(wrapper.find("#description input").val()).toContain(initialValues.description);
    }
  });
});
