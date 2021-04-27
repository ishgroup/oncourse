import CourseEditView from "../../../js/containers/entities/courses/components/CourseEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CourseEditView", () => {
  mockedEditView({
    entity: "Course",
    EditView: CourseEditView,
    record: mockecApi => mockecApi.db.getCourse(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#code").text()).toContain(initialValues.code);
      expect(wrapper.find("#tags").text()).toContain("No value");
      expect(wrapper.find("#enrolmentType").text()).toContain(initialValues.enrolmentType);
      expect(wrapper.find("#status").text()).toContain(initialValues.status);
      expect(wrapper.find("#dataCollectionRuleId").text()).toContain(initialValues.dataCollectionRuleName);
      expect(wrapper.find('#General input[type="checkbox"]').at(1).props().checked).toEqual(true);
      expect(wrapper.find("#brochureDescription").text()).toContain(initialValues.brochureDescription);
      expect(wrapper.find("#webDescription").text()).toContain(initialValues.webDescription);
    }
  });
});
