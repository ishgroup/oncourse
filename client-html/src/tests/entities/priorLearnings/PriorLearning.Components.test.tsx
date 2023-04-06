import PriorLearningEditView from "../../../js/containers/entities/priorLearnings/components/PriorLearningEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PriorLearningEditView", () => {
  mockedEditView({
    entity: "PriorLearning",
    EditView: PriorLearningEditView,
    record: mockecApi => mockecApi.db.getPriorLearning(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        title: initialValues.title,
        contactId: initialValues.contactName,
        qualificationName: initialValues.qualificationName,
        qualificationNationalCode: initialValues.qualificationNationalCode,
        externalReference: initialValues.externalReference,
        outcomeIdTrainingOrg: initialValues.outcomeIdTrainingOrg.toString(),
        notes: initialValues.notes,
      });

      expect(screen.getByLabelText('Level').value).toBe(initialValues.qualificationLevel);
    }
  });
});
