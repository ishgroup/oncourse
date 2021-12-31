import ModulesEditView from "../../../js/containers/entities/modules/components/ModulesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ModulesEditView", () => {
  mockedEditView({
    entity: "Module",
    EditView: ModulesEditView,
    record: mockecApi => mockecApi.db.getModule(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        title: initialValues.title,
        creditPoints: initialValues.creditPoints,
        nationalCode: initialValues.nationalCode,
        expiryDays: initialValues.expiryDays,
        fieldOfEducation: initialValues.fieldOfEducation,
        nominalHours: initialValues.nominalHours,
        specialization: initialValues.specialization,
        type: initialValues.type,
        isOffered: initialValues.isOffered,
      });
    }
  });
});
