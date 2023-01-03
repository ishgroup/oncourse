import CourseEditView from "../../../js/containers/entities/courses/components/CourseEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CourseEditView", () => {
  mockedEditView({
    entity: "Course",
    EditView: CourseEditView,
    record: mockecApi => mockecApi.db.getCourse(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        code: initialValues.code,
        enrolmentType: initialValues.enrolmentType,
        status: initialValues.status,
        allowWaitingLists: initialValues.allowWaitingLists,
        brochureDescription: initialValues.brochureDescription,
      });
    }
  });
});
