import ModulesEditView from "../../../js/containers/entities/modules/components/ModulesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ModulesEditView", () => {
  mockedEditView({
    entity: "Module",
    EditView: ModulesEditView,
    record: mockecApi => mockecApi.db.getModule(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#title").text()).toContain(initialValues.title);
      expect(wrapper.find("#nationalCode").text()).toContain(initialValues.nationalCode);
      expect(wrapper.find("#fieldOfEducation").text()).toContain(initialValues.fieldOfEducation);
      expect(wrapper.find("#specialization").text()).toContain("No value");
      expect(wrapper.find("#creditPoints").text()).toContain("No value");
      expect(wrapper.find("#expiryDays").text()).toContain("No value");
      expect(wrapper.find("#nominalHours").text()).toContain(initialValues.nominalHours);
      expect(wrapper.find("#type").text()).toContain(initialValues.type);
      expect(wrapper.find('input[type="checkbox"]').at(0).props().checked).toEqual(initialValues.isOffered);
    }
  });
});
