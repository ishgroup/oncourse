import QualificationsEditView from "../../../js/containers/entities/qualifications/components/QualificationsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered QualificationsEditView", () => {
  mockedEditView({
    entity: "Qualification",
    EditView: QualificationsEditView,
    record: mockecApi => mockecApi.db.getQualification(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        type: initialValues.type,
        qualLevel: initialValues.qualLevel,
        fieldOfEducation: initialValues.fieldOfEducation,
        title: initialValues.title,
        specialization: initialValues.specialization,
        nationalCode: initialValues.nationalCode,
        nominalHours: initialValues.nominalHours,
        isOffered: initialValues.isOffered,
      });
    }
  });
});
