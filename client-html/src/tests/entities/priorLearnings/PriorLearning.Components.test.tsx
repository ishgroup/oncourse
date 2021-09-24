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
      expect(wrapper.find("#qualificationName input").val()).toContain("No value");
      expect(wrapper.find("#qualificationNationalCode input").val()).toContain("No value");
      expect(wrapper.find("#externalReference input").val()).toContain("No value");
      expect(wrapper.find(".textField").at(3).text()).toContain("No Value");
      expect(wrapper.find("#outcomeIdTrainingOrg input").val()).toContain("No value");
      expect(wrapper.find("#notes input").val()).toContain("No value");
    }
  });
});
