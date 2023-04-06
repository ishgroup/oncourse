import SurveyEditView from "../../../js/containers/entities/survey/components/SurveyEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SurveyEditView", () => {
  mockedEditView({
    entity: "Survey",
    EditView: SurveyEditView,
    record: mockecApi => mockecApi.db.getSurvey(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        'EditListItemForm-netPromoterScore': initialValues.netPromoterScore.toString(),
        'EditListItemForm-courseScore': initialValues.courseScore.toString(),
        'EditListItemForm-venueScore': initialValues.venueScore.toString(),
        'EditListItemForm-tutorScore': initialValues.tutorScore.toString(),
        comment: initialValues.comment,
        visibility: initialValues.visibility,
        testimonial: initialValues.testimonial,
      });
    }
  });
});
