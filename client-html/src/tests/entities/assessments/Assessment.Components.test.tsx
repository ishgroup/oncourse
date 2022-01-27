import AssessmentEditView from "../../../js/containers/entities/assessments/components/AssessmentEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered AssessmentEditView", () => {
  mockedEditView({
    entity: "Assessment",
    EditView: AssessmentEditView,
    record: mockecApi => mockecApi.db.getAssessment(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        code: initialValues.code,
        name: initialValues.name,
        description: initialValues.description,
        active: initialValues.active,
      });
    }
  });
});
