import AssessmentEditView from "../../../js/containers/entities/assessments/components/AssessmentEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered AssessmentEditView", () => {
  mockedEditView({
    entity: "Assessment",
    EditView: AssessmentEditView,
    record: mockecApi => mockecApi.db.getAssessment(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#code").text()).toContain(initialValues.code);
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#tags").text()).toContain(initialValues.tags[0].name);
      expect(wrapper.find('input[type="checkbox"]').props().checked).toEqual(initialValues.active);
      expect(wrapper.find("#description").text()).toContain(initialValues.description);
    }
  });
});
