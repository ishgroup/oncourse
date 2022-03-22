import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import { mockedEditView } from "../../common/MockedEditView.Components";
import CourseClassEditView from "../../../js/containers/entities/courseClasses/components/CourseClassEditView";

describe("Virtual rendered CourseClassEditView", () => {
  mockedEditView({
    entity: "CourseClass",
    EditView: CourseClassEditView,
    record: mockecApi => ({
      ...mockecApi.db.getCourseClass(1),
      trainingPlan: [],
      sessions: [],
      tutors: [],
      budget: [],
      studentAttendance: [],
      notes: [],
      assessments: [],
    }),
    render: ({
      screen, initialValues, formRoleName, fireEvent
    }) => {
      fireEvent.click(screen.getByTestId("expand-button-2"));

      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        courseId: initialValues.courseName,
        minStudentAge: initialValues.minStudentAge,
        maxStudentAge: initialValues.maxStudentAge,
        isActive: initialValues.isActive,
        isShownOnWeb: initialValues.isShownOnWeb,
        message: initialValues.message,
        webDescription: initialValues.webDescription,

        suppressAvetmissExport: initialValues.suppressAvetmissExport,
        deliveryMode: initialValues.deliveryMode,
        fundingSource: initialValues.fundingSource,
        vetFundingSourceStateID: initialValues.vetFundingSourceStateID,
        detBookingId: initialValues.detBookingId,
        vetCourseSiteID: initialValues.vetCourseSiteID.toString(),
        vetPurchasingContractID: initialValues.vetPurchasingContractID,
        vetPurchasingContractScheduleID: initialValues.vetPurchasingContractScheduleID,
        qualificationHours: initialValues.qualificationHours,
        nominalHours: initialValues.nominalHours,
        classroomHours: initialValues.classroomHours,
        studentContactHours: initialValues.studentContactHours,
        reportableHours: initialValues.reportableHours,
        // expiryDate: format(new Date(initialValues.expiryDate), III_DD_MMM_YYYY).toString(),
      });
    }
  });
});
