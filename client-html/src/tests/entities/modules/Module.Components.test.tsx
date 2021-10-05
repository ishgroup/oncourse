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
      expect(wrapper.find("#nominalHours input").val()).toContain(initialValues.nominalHours);
      expect(wrapper.find("#type input").val()).toContain(initialValues.type);
    }
  });
});
