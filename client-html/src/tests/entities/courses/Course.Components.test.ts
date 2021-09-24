import CourseEditView from "../../../js/containers/entities/courses/components/CourseEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CourseEditView", () => {
  mockedEditView({
    entity: "Course",
    EditView: CourseEditView,
    record: mockecApi => mockecApi.db.getCourse(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);
      expect(wrapper.find("#enrolmentType input").val()).toContain(initialValues.enrolmentType);
      expect(wrapper.find("#status input").val()).toContain(initialValues.status);
      expect(wrapper.find("#dataCollectionRuleId input").val()).toContain(initialValues.dataCollectionRuleId);
      expect(wrapper.find("#brochureDescription textarea").val()).toContain(initialValues.brochureDescription);
      expect(wrapper.find("#webDescription").text()).toContain(initialValues.webDescription);
    }
  });
});
