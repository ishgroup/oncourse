import ModulesEditView from "../../../js/containers/entities/modules/components/ModulesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ModulesEditView", () => {
  mockedEditView({
    entity: "Module",
    EditView: ModulesEditView,
    record: mockecApi => mockecApi.db.getModule(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#title input").val()).toContain(initialValues.title);
      expect(wrapper.find("#nationalCode input").val()).toContain(initialValues.nationalCode);
      expect(wrapper.find("#fieldOfEducation input").val()).toContain(initialValues.fieldOfEducation);
      expect(wrapper.find("#specialization input").val()).toContain("No value");
      expect(wrapper.find("#creditPoints input").val()).toContain("No value");
      expect(wrapper.find("#expiryDays input").val()).toContain("No value");
      expect(wrapper.find("#nominalHours input").val()).toContain(initialValues.nominalHours);
      expect(wrapper.find("#type input").val()).toContain(initialValues.type);
      expect(wrapper.find('input[type="checkbox"]').at(0).props().checked).toEqual(initialValues.isOffered);
    }
  });
});
