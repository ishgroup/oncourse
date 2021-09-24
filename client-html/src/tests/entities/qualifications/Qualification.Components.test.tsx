import QualificationsEditView from "../../../js/containers/entities/qualifications/components/QualificationsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered QualificationsEditView", () => {
  mockedEditView({
    entity: "Qualification",
    EditView: QualificationsEditView,
    record: mockecApi => mockecApi.db.getQualification(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#type input").val()).toContain(initialValues.type);
      expect(wrapper.find("#qualLevel input").val()).toContain(initialValues.qualLevel);
      expect(wrapper.find("#title input").val()).toContain(initialValues.title);
      expect(wrapper.find("#nationalCode input").val()).toContain(initialValues.nationalCode);
      expect(wrapper.find("#anzsco input").val()).toContain("No value");
      expect(wrapper.find("#fieldOfEducation input").val()).toContain("No value");
      expect(wrapper.find("#specialization input").val()).toContain("No value");
      expect(wrapper.find("#nominalHours input").val()).toContain(initialValues.nominalHours);

      expect(wrapper.find('input[type="checkbox"]').props().checked).toEqual(false);
    }
  });
});
