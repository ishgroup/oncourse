import { format } from "date-fns";
import { formatCurrency, III_DD_MMM_YYYY } from "ish-ui";
import ApplicationEditView from "../../../js/containers/entities/applications/components/ApplicationEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ApplicationEditView", () => {
  mockedEditView({
    entity: "Application",
    EditView: ApplicationEditView,
    record: mockecApi => mockecApi.db.getApplication(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        contactId: initialValues.studentName,
        courseId: initialValues.courseName,
        applicationDate: format(new Date(initialValues.applicationDate), III_DD_MMM_YYYY).toString(),
        status: initialValues.status,
        feeOverride: formatCurrency(initialValues.feeOverride, ""),
        enrolBy: format(new Date(initialValues.enrolBy), III_DD_MMM_YYYY).toString(),
        createdBy: initialValues.createdBy,
        reason: initialValues.reason,
      });
    }
  });
});
