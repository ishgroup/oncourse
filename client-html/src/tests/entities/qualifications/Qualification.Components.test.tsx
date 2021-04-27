import QualificationsEditView from "../../../js/containers/entities/qualifications/components/QualificationsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered QualificationsEditView", () => {
  mockedEditView({
    entity: "Qualification",
    EditView: QualificationsEditView,
    record: mockecApi => mockecApi.db.getQualification(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#type").text()).toContain(initialValues.type);
      expect(wrapper.find("#qualLevel").text()).toContain(initialValues.qualLevel);
      expect(wrapper.find("#title").text()).toContain(initialValues.title);
      expect(wrapper.find("#nationalCode").text()).toContain(initialValues.nationalCode);
      expect(wrapper.find("#anzsco").text()).toContain("No value");
      expect(wrapper.find("#fieldOfEducation").text()).toContain("No value");
      expect(wrapper.find("#specialization").text()).toContain("No value");
      expect(wrapper.find("#nominalHours").text()).toContain(initialValues.nominalHours);

      expect(wrapper.find('input[type="checkbox"]').props().checked).toEqual(false);
    }
  });
});
