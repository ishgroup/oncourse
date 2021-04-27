import PriorLearningEditView from "../../../js/containers/entities/priorLearnings/components/PriorLearningEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered PriorLearningEditView", () => {
  mockedEditView({
    entity: "PriorLearning",
    EditView: PriorLearningEditView,
    record: mockecApi => mockecApi.db.getPriorLearning(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#title").text()).toContain(initialValues.title);
      expect(wrapper.find("#contactId").text()).toContain(initialValues.contactName);
      expect(wrapper.find("#qualificationName").text()).toContain("No value");
      expect(wrapper.find("#qualificationNationalCode").text()).toContain("No value");
      expect(wrapper.find("#externalReference").text()).toContain("No value");
      expect(wrapper.find(".textField").at(3).text()).toContain("No Value");
      expect(wrapper.find("#outcomeIdTrainingOrg").text()).toContain("No value");
      expect(wrapper.find("#notes").text()).toContain("No value");
    }
  });
});
