import SurveyEditView from "../../../js/containers/entities/survey/components/SurveyEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SurveyEditView", () => {
  mockedEditView({
    entity: "Survey",
    EditView: SurveyEditView,
    record: mockecApi => mockecApi.db.getSurvey(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find(".textField").at(0).text()).toContain(initialValues.studentName);
      expect(wrapper.find("#EditListItemForm-netPromoterScore-1").text()).toContain("");
      expect(wrapper.find("#EditListItemForm-netPromoterScore-2").text()).toContain("");

      expect(wrapper.find("#comment").text()).toContain(initialValues.comment);
      expect(wrapper.find("#visibility").text()).toContain(initialValues.visibility);
      expect(wrapper.find("#testimonial").text()).toContain("No value");
    }
  });
});
