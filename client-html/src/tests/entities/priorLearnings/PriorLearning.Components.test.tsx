import PriorLearningEditView from "../../../js/containers/entities/priorLearnings/components/PriorLearningEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PriorLearningEditView", () => {
  mockedEditView({
    entity: "PriorLearning",
    EditView: PriorLearningEditView,
    record: mockecApi => mockecApi.db.getPriorLearning(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#title input").val()).toContain(initialValues.title);
      expect(wrapper.find("#contactId input").val()).toContain(initialValues.contactName);
    }
  });
});
